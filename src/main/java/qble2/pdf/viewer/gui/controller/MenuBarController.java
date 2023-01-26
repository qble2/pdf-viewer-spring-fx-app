package qble2.pdf.viewer.gui.controller;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import lombok.extern.slf4j.Slf4j;
import qble2.pdf.viewer.gui.PdfViewerConfig;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.LoadDirectoryEvent;
import qble2.pdf.viewer.gui.event.ReLoadDirectoryEvent;

@Component
@Slf4j
public class MenuBarController implements Initializable, EventListener {

  @Autowired
  private EventBusFx eventBusFx;

  @FXML
  private Button selectDirectoryButton;

  @FXML
  private Button reloadDirectoryButton;

  //
  private DirectoryChooser directoryChooser;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);

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
      log.info("selected directory:\t{}", selectedDirectoryPath.toString());

      PdfViewerConfig.getInstance().saveLastUsedDirectory(selectedDirectoryPath.toString());
      eventBusFx.notify(new LoadDirectoryEvent(selectedDirectoryPath));
    }
  }

  @FXML
  private void reloadDirectory() {
    eventBusFx.notify(new ReLoadDirectoryEvent());
  }

  /////
  /////
  /////

}
