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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import lombok.extern.slf4j.Slf4j;
import qble2.pdf.viewer.gui.PdfViewerConfig;
import qble2.pdf.viewer.gui.event.AppColorChangedEvent;
import qble2.pdf.viewer.gui.event.DirectoryChangedEvent;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.FileSelectionChangedEvent;
import qble2.pdf.viewer.gui.event.FullScreenModeEvent;
import qble2.pdf.viewer.gui.event.LoadDirectoryEvent;
import qble2.pdf.viewer.gui.event.OpenSettingsDialogEvent;
import qble2.pdf.viewer.gui.event.ReLoadCurrentDirectoryEvent;
import qble2.pdf.viewer.gui.event.RequestFullScreenModeEvent;
import qble2.pdf.viewer.gui.event.SplitPdfFileEvent;

@Component
@Slf4j
public class MenuBarController implements Initializable, EventListener {

  @Autowired
  private EventBusFx eventBusFx;

  @Autowired
  private PdfViewerConfig pdfViewerConfig;

  @FXML
  private Parent root;

  @FXML
  private Button loadDirectoryButton;

  @FXML
  private Label currentDirectoryLabel;

  @FXML
  private Button reloadCurrentDirectoryButton;

  @FXML
  private Button splitSelectedPdfFileButton;

  @FXML
  private Button enterFullScreenModeButton;

  @FXML
  private Button selectExternalPdfFileToSplitButton;

  @FXML
  private ColorPicker colorPicker;

  //
  private DirectoryChooser directoryChooser;
  private FileChooser fileChooser;
  private ObjectProperty<Path> currentDirectoryPathObjectProperty = new SimpleObjectProperty<>();
  private ObjectProperty<Path> selectedPdfFilePathObjectProperty = new SimpleObjectProperty<>();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);

    colorPicker.setValue(Color.valueOf(pdfViewerConfig.getAppColor()));

    directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Select directory to load");

    fileChooser = new FileChooser();
    fileChooser.setTitle("Select PDF file to split");
    final ExtensionFilter filter = new ExtensionFilter("PDF file", "*.pdf");
    fileChooser.getExtensionFilters().add(filter);
    fileChooser.setSelectedExtensionFilter(filter);

    //
    root.managedProperty().bind(root.visibleProperty());
    reloadCurrentDirectoryButton.managedProperty()
        .bind(reloadCurrentDirectoryButton.visibleProperty());
    currentDirectoryLabel.managedProperty().bind(currentDirectoryLabel.visibleProperty());

    currentDirectoryLabel.visibleProperty().bind(currentDirectoryPathObjectProperty.isNotNull());

    reloadCurrentDirectoryButton.disableProperty()
        .bind(currentDirectoryPathObjectProperty.isNull());
    splitSelectedPdfFileButton.disableProperty().bind(selectedPdfFilePathObjectProperty.isNull());
    enterFullScreenModeButton.disableProperty().bind(selectedPdfFilePathObjectProperty.isNull());

    // Function<Path, String> converter = p -> p != null ? p.toString() : StringUtils.EMPTY;
    // currentDirectoryLabel.textProperty()
    // .bind(Bindings.createObjectBinding(
    // () -> converter.apply(currentDirectoryPathObjectProperty.get()),
    // currentDirectoryPathObjectProperty));
    currentDirectoryLabel.textProperty().bind(currentDirectoryPathObjectProperty.asString());

    //
    colorPicker.valueProperty().addListener((obs, oldValue, newValue) -> {
      eventBusFx.notify(new AppColorChangedEvent(newValue));
    });
  }

  /////
  /////
  /////

  @FXML
  private void loadDirectory() {
    File selectedDirectory =
        directoryChooser.showDialog(loadDirectoryButton.getScene().getWindow());
    if (selectedDirectory != null) {
      Path selectedDirectoryPath = selectedDirectory.toPath();
      log.info("selected directory:\t{}", selectedDirectoryPath.toString());

      eventBusFx.notify(new LoadDirectoryEvent(selectedDirectoryPath));
    }
  }

  @FXML
  private void reloadCurrentDirectory() {
    eventBusFx.notify(new ReLoadCurrentDirectoryEvent());
  }

  @FXML
  private void splitSelectedPdfFile() {
    eventBusFx.notify(new SplitPdfFileEvent(selectedPdfFilePathObjectProperty.get()));
  }

  @FXML
  private void selectExternalPdfFileToSplit() {
    File selectedPdfFile = fileChooser.showOpenDialog(loadDirectoryButton.getScene().getWindow());
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
  private void openSettingsDialog() {
    eventBusFx.notify(new OpenSettingsDialogEvent());
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processDirectoryChangedEvent(DirectoryChangedEvent event) {
    currentDirectoryPathObjectProperty.set(event.getDirectoryPath());
  }

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
