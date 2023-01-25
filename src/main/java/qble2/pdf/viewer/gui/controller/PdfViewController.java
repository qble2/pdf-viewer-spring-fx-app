package qble2.pdf.viewer.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.ResourceBundle;
import org.springframework.stereotype.Component;
import com.dlsc.pdfviewfx.PDFView;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import lombok.extern.slf4j.Slf4j;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.FileSelectionChangedEvent;

@Component
@Slf4j
public class PdfViewController implements Initializable, EventListener {

  @FXML
  private PDFView pdfView;

  //
  private ObjectProperty<Path> selectedPdfFilePathObjectProperty = new SimpleObjectProperty<>();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    EventBusFx.getInstance().registerListener(this);

    pdfView.visibleProperty().bind(selectedPdfFilePathObjectProperty.isNotNull());
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processFileSelectionChangedEvent(FileSelectionChangedEvent event) throws IOException {
    Path pdfFilePath = event.getFilePath();
    if (pdfFilePath != null) {
      log.info("loading pdf file:\t{}", pdfFilePath.toString());
      pdfView.load(pdfFilePath.toFile());
    } else {
      unloadPdf();
    }

    selectedPdfFilePathObjectProperty.set(pdfFilePath);
  }

  /////
  /////
  /////

  private void unloadPdf() {
    pdfView.unload();
  }

}
