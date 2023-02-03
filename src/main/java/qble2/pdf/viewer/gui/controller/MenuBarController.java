package qble2.pdf.viewer.gui.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import lombok.extern.slf4j.Slf4j;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.FileSelectionChangedEvent;
import qble2.pdf.viewer.gui.event.FullScreenModeEvent;
import qble2.pdf.viewer.gui.event.LoadDirectoryEvent;
import qble2.pdf.viewer.gui.event.OpenConfigDialogEvent;
import qble2.pdf.viewer.gui.event.ReLoadDirectoryEvent;
import qble2.pdf.viewer.gui.event.RequestFullScreenModeEvent;
import qble2.pdf.viewer.gui.event.SplitPdfFileEvent;

@Component
@Slf4j
public class MenuBarController implements Initializable, EventListener {

  @Autowired
  private EventBusFx eventBusFx;

  @FXML
  private Parent root;

  @FXML
  private Button selectDirectoryButton;

  @FXML
  private Button reloadDirectoryButton;

  @FXML
  private Button splitSelectedPdfFileButton;

  @FXML
  private Button selectExternalPdfFileToSplitButton;

  //
  private DirectoryChooser directoryChooser;
  private FileChooser fileChooser;
  private ObjectProperty<Path> selectedPdfFilePathObjectProperty = new SimpleObjectProperty<>();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);

    directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Select directory to load");

    fileChooser = new FileChooser();
    fileChooser.setTitle("Select PDF file to split");
    final ExtensionFilter filter = new ExtensionFilter("PDF Files", "*.pdf");
    fileChooser.getExtensionFilters().add(filter);
    fileChooser.setSelectedExtensionFilter(filter);

    //
    root.managedProperty().bind(root.visibleProperty());
    splitSelectedPdfFileButton.visibleProperty()
        .bind(selectedPdfFilePathObjectProperty.isNotNull());
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

      eventBusFx.notify(new LoadDirectoryEvent(selectedDirectoryPath));
    }
  }

  @FXML
  private void reloadDirectory() {
    eventBusFx.notify(new ReLoadDirectoryEvent());
  }

  @FXML
  private void splitSelectedPdfFile() {
    eventBusFx.notify(new SplitPdfFileEvent(selectedPdfFilePathObjectProperty.get()));
  }

  @FXML
  private void selectExternalPdfFileToSplit() {
    File selectedPdfFile = fileChooser.showOpenDialog(selectDirectoryButton.getScene().getWindow());
    if (selectedPdfFile != null) {
      Path selectedPdfFilePath = selectedPdfFile.toPath();
      log.info("selected PDF file to split:\t{}", selectedPdfFilePath.toString());
      eventBusFx.notify(new SplitPdfFileEvent(selectedPdfFilePath));
    }
  }

  @FXML
  private void enterFullScreenMode() {
    eventBusFx.notify(new RequestFullScreenModeEvent());
  }

  @FXML
  private void openConfigDialog() {
    eventBusFx.notify(new OpenConfigDialogEvent());
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processFileSelectionChangedEvent(FileSelectionChangedEvent event)
      throws IOException, InterruptedException {
    Path pdfFilePath = event.getFilePath();
    selectedPdfFilePathObjectProperty.set(pdfFilePath);
  }

  @Subscribe
  public void processFullScreenModeEvent(FullScreenModeEvent event) {
    this.root.setVisible(!event.isFullScreen());
  }

}
