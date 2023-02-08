package qble2.pdf.viewer.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.dlsc.pdfviewfx.PDFView;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import qble2.pdf.viewer.gui.LoadPdfFileTask;
import qble2.pdf.viewer.gui.PdfViewerConfig;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.FileSelectionChangedEvent;
import qble2.pdf.viewer.gui.event.FullScreenModeEvent;

@Component
public class PdfViewController implements Initializable, EventListener {

  @Autowired
  private EventBusFx eventBusFx;

  @Autowired
  private PdfViewerConfig pdfViewerConfig;

  @FXML
  private PDFView pdfView;

  //
  private ObjectProperty<Path> selectedPdfFilePathObjectProperty = new SimpleObjectProperty<>();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);

    pdfView.thumbnailSizeProperty().set(pdfViewerConfig.getPdfViewThumbnailsSize());

    pdfView.visibleProperty().bind(selectedPdfFilePathObjectProperty.isNotNull());
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processFileSelectionChangedEvent(FileSelectionChangedEvent event)
      throws IOException, InterruptedException {
    Path pdfFilePath = event.getFilePath();
    selectedPdfFilePathObjectProperty.set(pdfFilePath);

    if (pdfFilePath != null) {
      new LoadPdfFileTask(eventBusFx, pdfView, pdfFilePath).start();
    } else {
      if (pdfView.getDocument() != null) {
        pdfView.unload();
      }
    }
  }

  @Subscribe
  public void processFullScreenModeEvent(FullScreenModeEvent event) {
    boolean isPdfViewThumbnailsVisible = !event.isFullScreen()
        || (event.isFullScreen() && pdfViewerConfig.isPdfThumbnailsVisibleInFullScreenMode());
    this.pdfView.setShowThumbnails(isPdfViewThumbnailsVisible);

    boolean isPdfViewToolBarVisible = !event.isFullScreen() || (event.isFullScreen()
        && pdfViewerConfig.isPdfViewToolBarVisibleInFullScreenModeCheckbox());
    this.pdfView.setShowToolBar(isPdfViewToolBarVisible);

    this.pdfView.setThumbnailSize(
        event.isFullScreen() ? pdfViewerConfig.getPdfViewThumbnailsSizeInFullScreenMode()
            : pdfViewerConfig.getPdfViewThumbnailsSize());
  }

}
