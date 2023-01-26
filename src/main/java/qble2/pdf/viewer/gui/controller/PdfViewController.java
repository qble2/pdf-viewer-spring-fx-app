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
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import lombok.extern.slf4j.Slf4j;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.FileSelectionChangedEvent;
import qble2.pdf.viewer.gui.event.TaskDoneEvent;
import qble2.pdf.viewer.gui.event.TaskRunningEvent;

@Component
@Slf4j
public class PdfViewController implements Initializable, EventListener {

  @Autowired
  private EventBusFx eventBusFx;

  @FXML
  private PDFView pdfView;

  //
  private ObjectProperty<Path> selectedPdfFilePathObjectProperty = new SimpleObjectProperty<>();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);

    pdfView.visibleProperty().bind(selectedPdfFilePathObjectProperty.isNotNull());

    pdfView.documentProperty().addListener((obs, oldValue, newValue) -> {
      eventBusFx.notify(new TaskDoneEvent());
    });
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
      log.info("loading pdf file:\t{}", pdfFilePath.toString());
      eventBusFx.notify(new TaskRunningEvent());

      // PDFView.load requires to be run on the FX Application thread
      // cannot make an async task out of it
      // as a result, GUI will freeze during large pdf file loading
      new Thread(() -> {
        try {
          // workaround: giving time to the FX Application thread to give some user feedback
          // before it's beeing blocked by PDFView.load task
          Thread.sleep(100);
        } catch (InterruptedException e) {
          log.error("an error has occured");
        }
        Platform.runLater(() -> pdfView.load(pdfFilePath.toFile()));
      }).start();
    } else {
      pdfView.unload();
    }
  }

  /////
  /////
  /////

}
