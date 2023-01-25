package qble2.document.viewer.gui.controller;

import java.net.URL;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.ResourceBundle;
import org.springframework.stereotype.Component;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import qble2.document.viewer.gui.PdfViewerConfig;
import qble2.document.viewer.gui.ViewConstant;
import qble2.document.viewer.gui.event.EventBusFx;
import qble2.document.viewer.gui.event.FileSelectionChangedEvent;
import qble2.document.viewer.gui.event.LoadDirectoryEvent;

@Component
@Slf4j
public class MainController implements Initializable, EventListener {

  private Stage stage;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    EventBusFx.getInstance().registerListener(this);

    String lastDirectoryPath = PdfViewerConfig.getInstance().getLastUsedDirectory();
    if (lastDirectoryPath != null) {
      log.info("found last directory used:\t{}", lastDirectoryPath);
      EventBusFx.getInstance().notify(new LoadDirectoryEvent(Path.of(lastDirectoryPath)));
    }
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processFileSelectionChangedEvent(FileSelectionChangedEvent event) {
    String fileName = ViewConstant.APP_TITLE;
    if (event.getFilePath() != null) {
      fileName = event.getFilePath().getFileName().toString();
    }
    this.stage.setTitle(fileName);
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  /////
  /////
  /////

}
