package qble2.pdf.viewer.gui.controller;

import java.net.URL;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.List;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import qble2.pdf.viewer.gui.AutoCompleteTextField;
import qble2.pdf.viewer.gui.AutoCompleteTextField.AutoCompletedEvent;
import qble2.pdf.viewer.gui.PdfViewerConfig;
import qble2.pdf.viewer.gui.event.AutoCompleteSelectionChangedEvent;
import qble2.pdf.viewer.gui.event.ClearFileSelectionEvent;
import qble2.pdf.viewer.gui.event.DirectoryChangedEvent;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.FilesListChangedEvent;
import qble2.pdf.viewer.gui.event.FullScreenModeEvent;

@Component
public class FilesNavigationPaneViewController implements Initializable, EventListener {

  // matching PDFView
  private static final String SEARCH_PROMPT_TEXT = "Search ...";

  @Autowired
  private EventBusFx eventBusFx;

  @Autowired
  private PdfViewerConfig pdfViewerConfig;

  @FXML
  private Parent root;

  @FXML
  private Label expandListViewLabel;

  @FXML
  private Label collapseListViewLabel;

  @FXML
  private Parent tabPane;

  @FXML
  private Tab listViewTab;

  @FXML
  private Tab treeViewTab;

  @FXML
  private HBox autoCompleteBox;

  // not using ControlsFX
  private AutoCompleteTextField autoCompleteTextField;

  @FXML
  private Button clearAutoCompleteInputButton;

  @FXML
  private CheckBox enableAutoCompleteSuggestionsCheckBox;

  //
  private ObjectProperty<Path> currentDirectoryPathObjectProperty = new SimpleObjectProperty<>();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);

    // cannot be done with Scene Builder when Tab's content comes from an included fxml file
    ImageView listViewTabImageView = new ImageView();
    listViewTabImageView.getStyleClass().add("image-view-flat-view");
    listViewTab.setGraphic(listViewTabImageView);

    ImageView treeViewTabImageView = new ImageView();
    treeViewTabImageView.getStyleClass().add("image-view-hierarchical-view");
    treeViewTab.setGraphic(treeViewTabImageView);

    // custom control
    createAutoCompleteTextField();

    root.managedProperty().bind(root.visibleProperty());
    expandListViewLabel.managedProperty().bind(expandListViewLabel.visibleProperty());
    collapseListViewLabel.managedProperty().bind(collapseListViewLabel.visibleProperty());
    tabPane.managedProperty().bind(tabPane.visibleProperty());
    autoCompleteBox.managedProperty().bind(autoCompleteBox.visibleProperty());

    expandListViewLabel.visibleProperty().bind(tabPane.visibleProperty().not());
    collapseListViewLabel.visibleProperty().bind(tabPane.visibleProperty());
    autoCompleteBox.visibleProperty().bind(tabPane.visibleProperty());

    // stage would not resize properly
    // root.visibleProperty().bind(currentDirectoryPathObjectProperty.isNotNull());

    root.disableProperty().bind(currentDirectoryPathObjectProperty.isNull());
  }

  @FXML
  private void clearAutoCompleteSelection() {
    autoCompleteTextField.clear();
    eventBusFx.notify(new ClearFileSelectionEvent());
  }

  @FXML
  private void expandCollapseListView() {
    tabPane.setVisible(!tabPane.isVisible());
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processDirectoryChangedEvent(DirectoryChangedEvent event) {
    currentDirectoryPathObjectProperty.set(event.getDirectoryPath());
  }

  @Subscribe
  public void processFullScreenModeEvent(FullScreenModeEvent event) {
    boolean isVisible = !event.isFullScreen()
        || (event.isFullScreen() && pdfViewerConfig.isFilesNavigationPaneVisibleInFullScreenMode());
    this.root.setVisible(isVisible);
  }

  /////
  /////
  /////

  private void createAutoCompleteTextField() {
    enableAutoCompleteSuggestionsCheckBox
        .setSelected(pdfViewerConfig.isAutoCompleteSuggestionsEnabledAtStartup());

    autoCompleteTextField = new AutoCompleteTextField(SEARCH_PROMPT_TEXT,
        enableAutoCompleteSuggestionsCheckBox.selectedProperty());
    HBox.setHgrow(autoCompleteTextField, Priority.ALWAYS);
    autoCompleteTextField.maxWidth(Double.MAX_VALUE);
    autoCompleteTextField.textProperty().addListener((obs, oldValue, newValue) -> {
      // if checkbox selected, let AutoCompletedEvent handle notifications
      if (!enableAutoCompleteSuggestionsCheckBox.isSelected()) {
        eventBusFx.notify(new AutoCompleteSelectionChangedEvent(newValue));
      }
    });
    autoCompleteTextField.addEventHandler(AutoCompleteTextField.AutoCompletedEvent.AUTO_COMPLETED,
        new EventHandler<AutoCompleteTextField.AutoCompletedEvent>() {
          @Override
          public void handle(AutoCompletedEvent event) {
            eventBusFx.notify(new AutoCompleteSelectionChangedEvent(event.getCompletion()));
            root.requestFocus();
          }
        });
    autoCompleteBox.getChildren().add(0, autoCompleteTextField);
  }

  @Subscribe
  private void processFilesListChangedEvent(FilesListChangedEvent event) {
    List<String> suggestions =
        event.getFilesList().stream().map(path -> path.getFileName().toString()).toList();
    autoCompleteTextField.updateSuggestions(suggestions);
  }

}
