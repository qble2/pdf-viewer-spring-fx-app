package qble2.document.viewer.gui.controller;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.ResourceBundle;
import org.springframework.stereotype.Component;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import lombok.extern.slf4j.Slf4j;
import qble2.document.viewer.gui.PdfViewerConfig;
import qble2.document.viewer.gui.event.EventBusFx;
import qble2.document.viewer.gui.event.LoadDirectoryEvent;

@Component
@Slf4j
public class MenuBarController implements Initializable, EventListener {

  @FXML
  private Button selectDirectoryButton;

  //
  private DirectoryChooser directoryChooser;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    EventBusFx.getInstance().registerListener(this);

    directoryChooser = new DirectoryChooser();
  }

  /////
  /////
  /////

  @FXML
  private void selectDirectory() {
    File selectedDirectory =
        directoryChooser.showDialog(selectDirectoryButton.getScene().getWindow());
    if (selectedDirectory != null) {
      Path selectedDirectoryPath = selectedDirectory.toPath();
      log.info("selected directoryPath:\t{}", selectedDirectoryPath.toString());

      PdfViewerConfig.getInstance().saveLastUsedDirectory(selectedDirectoryPath.toString());
      EventBusFx.getInstance().notify(new LoadDirectoryEvent(selectedDirectoryPath));
    }
  }

  /////
  /////
  /////

}
