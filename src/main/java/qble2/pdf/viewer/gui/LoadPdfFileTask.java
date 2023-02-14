package qble2.pdf.viewer.gui;

import java.nio.file.Path;
import com.dlsc.pdfviewfx.PDFView;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.PageSelectionChangedEvent;
import qble2.pdf.viewer.gui.event.TaskDoneEvent;
import qble2.pdf.viewer.gui.event.TaskRunningEvent;

@Slf4j
public class LoadPdfFileTask {

  private EventBusFx eventBusFx;
  private PDFView pdfView;
  private Path pdfFilePath;

  public LoadPdfFileTask(EventBusFx eventBusFx, PDFView pdfView, Path pdfFilePath) {
    this.eventBusFx = eventBusFx;
    this.pdfView = pdfView;
    this.pdfFilePath = pdfFilePath;

    this.pdfView.documentProperty().addListener((obs, oldValue, newValue) -> {
      this.eventBusFx.notify(new TaskDoneEvent());
    });

    this.pdfView.pageProperty().addListener((obs, oldValue, newValue) -> {
      this.eventBusFx.notify(new PageSelectionChangedEvent(newValue.intValue()));
    });
  }

  public void start() {
    log.info("loading PDF file:\t{}", pdfFilePath.toString());
    eventBusFx.notify(new TaskRunningEvent());

    // PDFView.load requires to be run on the FX Application thread
    // cannot make an async task out of it
    // as a result, GUI will freeze during large PDF file loading
    new Thread(() -> {
      try {
        // workaround: giving time to the FX Application thread to give some user feedback
        // before it's beeing blocked by PDFView.load task
        Thread.sleep(100);
      } catch (InterruptedException e) {
        log.error("an error has occured", e);
      }

      Platform.runLater(() -> {
        try {
          pdfView.load(pdfFilePath.toFile());
        } catch (Exception e) {
          // FileNotFoundException for example
          log.error("an error has occured", e);
          pdfView.unload();
          eventBusFx.notify(new TaskDoneEvent());
        }
      });
    }).start();
  }

}
