package qble2.pdf.viewer.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.eventbus.Subscribe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.extern.slf4j.Slf4j;
import qble2.pdf.viewer.gui.AutoCompleteTextField;
import qble2.pdf.viewer.gui.AutoCompleteTextField.AutoCompletedEvent;
import qble2.pdf.viewer.gui.DirectoryService;
import qble2.pdf.viewer.gui.FilePathCellFactory;
import qble2.pdf.viewer.gui.ViewConstant;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.FileSelectionChangedEvent;
import qble2.pdf.viewer.gui.event.LoadDirectoryEvent;

@Component
@Slf4j
public class FileListController implements Initializable, EventListener {

  // The Java Virtual Machine exits when the only threads running are all daemon threads.
  // This way, there is no need to manually shutdown the executor on application exit
  private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
    final Thread thread = new Thread(r);
    thread.setDaemon(true);
    return thread;
  });

  @Autowired
  private DirectoryService directoryService;

  @FXML
  private HBox filterBox;

  // not using ControlsFX
  private AutoCompleteTextField autoCompleteTextField;
  private Label clearAutoCompleteInputLabel;

  @FXML
  private ListView<Path> fileListView;

  //
  private ObservableList<Path> observableFileList;
  private FilteredList<Path> filteredFileList;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    EventBusFx.getInstance().registerListener(this);

    initFileListView();
    initAutoCompleteTextField();
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processLoadDirectoryEvent(LoadDirectoryEvent event) throws IOException {
    Task<List<Path>> task = new Task<>() {
      @Override
      protected List<Path> call() throws Exception {
        return directoryService.loadDirectory(event.getDirectoryPath());
      }
    };
    task.setOnSucceeded(e -> {
      log.info("directory has been loaded, found {} files", task.getValue().size());
      observableFileList.clear();
      observableFileList.addAll(task.getValue());

      updateAutocompleteSuggestions(task.getValue());
    });
    task.setOnFailed(e -> {
      log.info("loading directory failed.");
    });

    executor.submit(task);
  }

  /////
  /////
  /////

  private void initFileListView() {
    observableFileList = FXCollections.observableArrayList();
    filteredFileList = new FilteredList<>(observableFileList);
    fileListView.setItems(filteredFileList);

    fileListView.setCellFactory(new FilePathCellFactory());
    fileListView.getSelectionModel().selectedItemProperty()
        .addListener((obs, oldValue, newValue) -> {
          EventBusFx.getInstance().notify(new FileSelectionChangedEvent(newValue));
        });
  }

  private void initAutoCompleteTextField() {
    autoCompleteTextField = new AutoCompleteTextField(ViewConstant.SEARCH_PROMPT_TEXT);
    HBox.setHgrow(autoCompleteTextField, Priority.ALWAYS);
    autoCompleteTextField.setMaxHeight(Double.MAX_VALUE);
    autoCompleteTextField.addEventHandler(AutoCompleteTextField.AutoCompletedEvent.AUTO_COMPLETED,
        new EventHandler<AutoCompleteTextField.AutoCompletedEvent>() {
          @Override
          public void handle(AutoCompletedEvent event) {
            filteredFileList
                .setPredicate(path -> path.getFileName().toString().equals(event.getCompletion()));
            fileListView.getSelectionModel().select(0);
          }
        });
    filterBox.getChildren().add(autoCompleteTextField);

    Image clearAutoCompleteImage = new Image(
        getClass().getResource("/image/material/outline_close_black_24dp.png").toExternalForm());
    ImageView clearAutoCompleteImageView = new ImageView(clearAutoCompleteImage);
    clearAutoCompleteInputLabel = new Label();
    clearAutoCompleteInputLabel.setMaxHeight(Double.MAX_VALUE);
    clearAutoCompleteInputLabel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    clearAutoCompleteInputLabel.setGraphic(clearAutoCompleteImageView);
    clearAutoCompleteInputLabel.setTooltip(new Tooltip("clear text"));
    clearAutoCompleteInputLabel.visibleProperty()
        .bind(autoCompleteTextField.textProperty().isNotEmpty());
    clearAutoCompleteInputLabel.setOnMouseClicked(e -> {
      autoCompleteTextField.clear();
      filteredFileList.setPredicate(x -> true);
    });
    filterBox.getChildren().add(clearAutoCompleteInputLabel);
  }

  private void updateAutocompleteSuggestions(List<Path> fileList) {
    List<String> suggestions =
        fileList.stream().map(path -> path.getFileName().toString()).toList();
    autoCompleteTextField.updateSuggestions(suggestions);
  }

}
