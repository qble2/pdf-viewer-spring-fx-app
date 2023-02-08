package qble2.pdf.viewer.gui.controller;

import java.net.URL;
import java.util.EventListener;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.Setter;
import qble2.pdf.viewer.StageInitializer;
import qble2.pdf.viewer.gui.PdfViewerConfig;
import qble2.pdf.viewer.gui.event.DarkModeEvent;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.FileSelectionChangedEvent;
import qble2.pdf.viewer.gui.event.RequestFullScreenModeEvent;

@Component
public class MainController implements Initializable, EventListener {

  @Autowired
  private EventBusFx eventBusFx;

  @Autowired
  private PdfViewerConfig pdfViewerConfig;

  //
  @Setter
  private Stage stage;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processFileSelectionChangedEvent(FileSelectionChangedEvent event) {
    String fileName = StageInitializer.APP_TITLE;
    if (event.getFilePath() != null) {
      fileName = event.getFilePath().getFileName().toString();
    }
    this.stage.setTitle(fileName);
  }

  @Subscribe
  public void processRequestFullScreenModeEvent(RequestFullScreenModeEvent event) {
    this.stage.setFullScreen(true);
  }

  @Subscribe
  public void processDarkModeEvent(DarkModeEvent event) {
    if (event.isDarkMode()) {
      this.stage.getScene().getStylesheets()
          .add(getClass().getResource("/css/dark.css").toExternalForm());
      this.stage.getScene().getStylesheets()
          .remove(getClass().getResource("/css/light.css").toExternalForm());
      pdfViewerConfig.setDarkModeEnabled(true);
    } else {
      this.stage.getScene().getStylesheets()
          .add(getClass().getResource("/css/light.css").toExternalForm());
      this.stage.getScene().getStylesheets()
          .remove(getClass().getResource("/css/dark.css").toExternalForm());
      pdfViewerConfig.setDarkModeEnabled(false);
    }
  }

  /////
  /////
  /////

}
