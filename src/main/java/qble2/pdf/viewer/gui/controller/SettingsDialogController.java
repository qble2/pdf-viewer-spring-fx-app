package qble2.pdf.viewer.gui.controller;

import java.net.URL;
import java.util.EventListener;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.stage.Stage;
import lombok.Setter;
import qble2.pdf.viewer.gui.PdfViewerConfig;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.OpenSettingsDialogEvent;
import qble2.pdf.viewer.gui.event.StageShownEvent;

@Component
public class SettingsDialogController implements Initializable, EventListener {

  @Autowired
  private EventBusFx eventBusFx;

  @Autowired
  private PdfViewerConfig pdfViewerConfig;

  @FXML
  private DialogPane dialogPane;

  @FXML
  private CheckBox isMenuBarVisibleInFullScreenModeCheckbox;

  @FXML
  private CheckBox isFilesNavigationPaneVisibleInFullScreenModeCheckBox;

  @FXML
  private CheckBox isPdfViewThumbnailsVisibleInFullScreenModeCheckBox;

  @FXML
  private CheckBox isPdfViewToolBarVisibleInFullScreenModeCheckbox;

  @FXML
  private CheckBox isFooterVisibleInFullScreenModeCheckbox;

  @FXML
  private TextField pdfViewThumbnailsSizeInFullScreenModeTextField;

  @FXML
  private CheckBox isMaximizeStageAtStartupCheckbox;

  @FXML
  private CheckBox isAutoCompleteSuggestionsEnabledAtStartupCheckbox;

  @FXML
  private CheckBox isExpandAllTreeViewItemsCheckbox;

  //
  @Setter
  private Stage stage;
  private Dialog<ButtonType> dialog;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);

    dialog = new Dialog<>();
    dialog.setDialogPane(dialogPane);
    dialog.setTitle("Edit settings");

    addIntegerTextFormatter(pdfViewThumbnailsSizeInFullScreenModeTextField);

    ButtonType applyButton = new ButtonType("Apply", ButtonData.OK_DONE);
    ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
    dialog.getDialogPane().getButtonTypes().addAll(applyButton, cancelButton);
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processStageShownEvent(StageShownEvent event) {
    this.dialog.initOwner(stage);
  }

  @Subscribe
  public void processOpenSettingsDialogEvent(OpenSettingsDialogEvent event) {
    // dialog controls preserve their edited values on cancel action
    // values have to be reset from config
    loadConfig();

    Optional<ButtonType> result = dialog.showAndWait();
    result.filter(response -> response.getButtonData() == ButtonData.OK_DONE)
        .ifPresent(response -> updateConfig());
  }

  /////
  /////
  /////

  private void loadConfig() {
    isFilesNavigationPaneVisibleInFullScreenModeCheckBox
        .setSelected(pdfViewerConfig.isFilesNavigationPaneVisibleInFullScreenMode());
    isPdfViewThumbnailsVisibleInFullScreenModeCheckBox
        .setSelected(pdfViewerConfig.isPdfThumbnailsVisibleInFullScreenMode());
    isPdfViewToolBarVisibleInFullScreenModeCheckbox
        .setSelected(pdfViewerConfig.isPdfViewToolBarVisibleInFullScreenModeCheckbox());
    isFooterVisibleInFullScreenModeCheckbox
        .setSelected(pdfViewerConfig.isFooterVisibleInFullScreenModeCheckbox());
    pdfViewThumbnailsSizeInFullScreenModeTextField
        .setText(String.valueOf(pdfViewerConfig.getPdfViewThumbnailsSizeInFullScreenMode()));
    isMaximizeStageAtStartupCheckbox.setSelected(pdfViewerConfig.isMaximizeStageAtStartup());
    isAutoCompleteSuggestionsEnabledAtStartupCheckbox
        .setSelected(pdfViewerConfig.isAutoCompleteSuggestionsEnabledAtStartup());
    isExpandAllTreeViewItemsCheckbox
        .setSelected(pdfViewerConfig.isExpandAllTreeViewItems());
  }

  private void updateConfig() {
    pdfViewerConfig.setFilesNavigationPaneVisibleInFullScreenMode(
        isFilesNavigationPaneVisibleInFullScreenModeCheckBox.isSelected());
    pdfViewerConfig.setPdfThumbnailsVisibleInFullScreenMode(
        isPdfViewThumbnailsVisibleInFullScreenModeCheckBox.isSelected());
    pdfViewerConfig.setPdfViewToolBarVisibleInFullScreenMode(
        isPdfViewToolBarVisibleInFullScreenModeCheckbox.isSelected());
    pdfViewerConfig
        .setFooterVisibleInFullScreenMode(isFooterVisibleInFullScreenModeCheckbox.isSelected());
    pdfViewerConfig.setPdfViewThumbnailsSizeInFullScreenMode(
        NumberUtils.toInt(pdfViewThumbnailsSizeInFullScreenModeTextField.getText(), 200));
    pdfViewerConfig.setMaximizeStageAtStartup(isMaximizeStageAtStartupCheckbox.isSelected());
    pdfViewerConfig.setAutoCompleteSuggestionsEnabledAtStartup(
        isAutoCompleteSuggestionsEnabledAtStartupCheckbox.isSelected());
    pdfViewerConfig
        .setExpandAllTreeViewItems(isExpandAllTreeViewItemsCheckbox.isSelected());
  }

  private void addIntegerTextFormatter(TextField textField) {
    UnaryOperator<Change> filter = change -> {
      String text = change.getText();
      if (text.matches("[0-9]*")) {
        return change;
      }

      return null;
    };

    // a formatter is dedicated to a single control
    TextFormatter<String> textFormatter = new TextFormatter<>(filter);
    textField.setTextFormatter(textFormatter);
  }

}
