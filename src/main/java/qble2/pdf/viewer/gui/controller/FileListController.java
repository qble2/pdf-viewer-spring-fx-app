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
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.extern.slf4j.Slf4j;
import qble2.pdf.viewer.gui.AutoCompleteTextField;
import qble2.pdf.viewer.gui.AutoCompleteTextField.AutoCompletedEvent;
import qble2.pdf.viewer.gui.FilePathCellFactory;
import qble2.pdf.viewer.gui.PdfViewerConfig;
import qble2.pdf.viewer.gui.ViewConstant;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.FileSelectionChangedEvent;
import qble2.pdf.viewer.gui.event.FullScreenModeEvent;
import qble2.pdf.viewer.gui.event.LoadDirectoryEvent;
import qble2.pdf.viewer.gui.event.ReLoadDirectoryEvent;
import qble2.pdf.viewer.gui.event.StageShownEvent;
import qble2.pdf.viewer.gui.event.TaskDoneEvent;
import qble2.pdf.viewer.gui.event.TaskRunningEvent;
import qble2.pdf.viewer.system.DirectoryService;

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
  private EventBusFx eventBusFx;

  @Autowired
  private PdfViewerConfig pdfViewerConfig;

  @Autowired
  private DirectoryService directoryService;

  @FXML
  private Parent root;

  @FXML
  private HBox autoCompleteBox;

  @FXML
  private Button clearAutoCompleteInputButton;

  // not using ControlsFX
  private AutoCompleteTextField autoCompleteTextField;

  @FXML
  private ScrollPane fileListViewScrollPane;

  @FXML
  private ListView<Path> fileListView;

  @FXML
  private Label expandListViewLabel;

  @FXML
  private Label collapseListViewLabel;

  //
  private Path lastDirectoryPath;
  private ObservableList<Path> observableFileList;
  private FilteredList<Path> filteredFileList;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);

    // custom control
    createAutoCompleteTextField();

    initFileListView();

    root.managedProperty().bind(root.visibleProperty());
    fileListViewScrollPane.managedProperty().bind(fileListViewScrollPane.visibleProperty());
    autoCompleteBox.managedProperty().bind(autoCompleteBox.visibleProperty());
    expandListViewLabel.managedProperty().bind(expandListViewLabel.visibleProperty());
    collapseListViewLabel.managedProperty().bind(collapseListViewLabel.visibleProperty());

    fileListViewScrollPane.visibleProperty().bind(fileListView.visibleProperty());
    autoCompleteBox.visibleProperty().bind(fileListView.visibleProperty());
    expandListViewLabel.visibleProperty().bind(fileListView.visibleProperty().not());
    collapseListViewLabel.visibleProperty().bind(fileListView.visibleProperty());
  }

  @FXML
  private void clearAutoCompleteSelection() {
    autoCompleteTextField.clear();
    filteredFileList.setPredicate(x -> true);
  }

  @FXML
  private void expandCollapseListView() {
    fileListView.setVisible(!fileListView.isVisible());
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processStageShownEvent(StageShownEvent event) {
    String lastDirectoryPath = pdfViewerConfig.getLastUsedDirectory();
    if (lastDirectoryPath != null) {
      log.info("found last directory used:\t{}", lastDirectoryPath);
      eventBusFx.notify(new LoadDirectoryEvent(Path.of(lastDirectoryPath)));
    }
  }

  @Subscribe
  public void processLoadDirectoryEvent(LoadDirectoryEvent event) throws IOException {
    runLoadDirectoryTask(event.getDirectoryPath());
  }

  @Subscribe
  public void processReLoadDirectoryEvent(ReLoadDirectoryEvent event) {
    if (lastDirectoryPath != null) {
      runLoadDirectoryTask(lastDirectoryPath);
    }
  }

  @Subscribe
  public void processFullScreenModeEvent(FullScreenModeEvent event) {
    boolean isVisible = !event.isFullScreen()
        || (event.isFullScreen() && pdfViewerConfig.isFileListVisibleInFullScreenMode());
    this.root.setVisible(isVisible);
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
          eventBusFx.notify(new FileSelectionChangedEvent(newValue));
        });
  }

  private void createAutoCompleteTextField() {
    autoCompleteTextField = new AutoCompleteTextField(ViewConstant.SEARCH_PROMPT_TEXT);
    HBox.setHgrow(autoCompleteTextField, Priority.ALWAYS);
    autoCompleteTextField.setPrefWidth(200d);
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
    autoCompleteBox.getChildren().add(0, autoCompleteTextField);
  }

  private void updateAutocompleteSuggestions(List<Path> fileList) {
    List<String> suggestions =
        fileList.stream().map(path -> path.getFileName().toString()).toList();
    autoCompleteTextField.updateSuggestions(suggestions);
  }

  private void runLoadDirectoryTask(Path directoryPath) {
    lastDirectoryPath = directoryPath;

    Task<List<Path>> task = new Task<>() {
      @Override
      protected List<Path> call() throws Exception {
        eventBusFx.notify(new TaskRunningEvent());
        return directoryService.loadDirectory(directoryPath);
      }
    };
    task.exceptionProperty().addListener((obs, oldValue, newValue) -> {
      if (newValue != null) {
        log.error("an error has occured", newValue);
      }
    });
    task.setOnSucceeded(e -> {
      log.info("directory has been loaded, found {} files", task.getValue().size());
      observableFileList.clear();
      observableFileList.addAll(task.getValue());

      updateAutocompleteSuggestions(task.getValue());

      pdfViewerConfig.saveLastUsedDirectory(directoryPath.toString());

      eventBusFx.notify(new TaskDoneEvent());
    });
    task.setOnFailed(e -> {
      log.info("loading directory failed");
      eventBusFx.notify(new TaskDoneEvent());
    });

    executor.submit(task);
  }

}
