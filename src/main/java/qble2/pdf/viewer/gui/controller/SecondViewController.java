package qble2.pdf.viewer.gui.controller;

import java.net.URL;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.ResourceBundle;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.dlsc.pdfviewfx.PDFView;
import com.google.common.eventbus.Subscribe;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import qble2.pdf.viewer.gui.LoadPdfFileTask;
import qble2.pdf.viewer.gui.PdfViewerConfig;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.PinPdfFileEvent;
import qble2.pdf.viewer.gui.event.SecondViewOpenedTabsCountChangedEvent;

@Component
public class SecondViewController implements Initializable, EventListener {

  @Autowired
  private EventBusFx eventBusFx;

  @Autowired
  private PdfViewerConfig pdfViewerConfig;

  @FXML
  private TabPane tabPane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);

    tabPane.setTabMaxHeight(Double.MAX_VALUE);

    tabPane.managedProperty().bind(tabPane.visibleProperty());
    tabPane.visibleProperty().bind(Bindings.isNotEmpty(tabPane.getTabs()));
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processPinPdfFileEvent(PinPdfFileEvent event) {
    String tabTitle = FilenameUtils.getBaseName(event.getPdfFilePath().getFileName().toString());
    boolean exists = tabPane.getTabs().stream().anyMatch(tab -> tab.getText().equals(tabTitle));
    if (!exists) {
      tabPane.getTabs().add(createNewTab(event.getPdfFilePath(), tabTitle));
      tabPane.getSelectionModel().selectLast();

      notifyOpenedTabsCountChanged();
    }
  }

  /////
  /////
  /////

  private Tab createNewTab(Path pdfFilePath, String tabTitle) {
    Tab tab = new Tab(tabTitle);

    PDFView pdfView = new PDFView();
    pdfView.thumbnailSizeProperty().set(pdfViewerConfig.getPdfViewThumbnailsSize());

    new LoadPdfFileTask(eventBusFx, pdfView, pdfFilePath).start();

    tab.setContent(pdfView);
    tab.setOnClosed(e -> {
      pdfView.unload();

      notifyOpenedTabsCountChanged();
    });

    return tab;
  }

  private void notifyOpenedTabsCountChanged() {
    eventBusFx.notify(new SecondViewOpenedTabsCountChangedEvent(tabPane.getTabs().size()));
  }

}
