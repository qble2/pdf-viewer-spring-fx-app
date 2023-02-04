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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import lombok.extern.slf4j.Slf4j;
import qble2.pdf.viewer.gui.FileListViewCellFactory;
import qble2.pdf.viewer.gui.PdfViewerConfig;
import qble2.pdf.viewer.gui.event.AutoCompleteSelectionChangedEvent;
import qble2.pdf.viewer.gui.event.ClearFileSelectionEvent;
import qble2.pdf.viewer.gui.event.DirectoryChangedEvent;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.FileSelectionChangedEvent;
import qble2.pdf.viewer.gui.event.FilesListChangedEvent;
import qble2.pdf.viewer.gui.event.LoadDirectoryEvent;
import qble2.pdf.viewer.gui.event.ReLoadCurrentDirectoryEvent;
import qble2.pdf.viewer.gui.event.StageShownEvent;
import qble2.pdf.viewer.gui.event.TaskDoneEvent;
import qble2.pdf.viewer.gui.event.TaskRunningEvent;
import qble2.pdf.viewer.system.DirectoryService;

@Component
@Slf4j
public class FilesListViewController implements Initializable, EventListener {

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
  private ScrollPane filesListViewScrollPane;

  @FXML
  private ListView<Path> fileListView;

  //
  private Path currentDirectoryPath;
  private ObservableList<Path> observableFileList;
  private FilteredList<Path> filteredFileList;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);

    initFileListView();

    root.managedProperty().bind(root.visibleProperty());
    filesListViewScrollPane.managedProperty().bind(filesListViewScrollPane.visibleProperty());

    filesListViewScrollPane.visibleProperty().bind(fileListView.visibleProperty());

  }

  /////
  ///// events
  /////

  @Subscribe
  public void processStageShownEvent(StageShownEvent event) {
    String currentDirectoryPath = pdfViewerConfig.getLastUsedDirectory();
    if (currentDirectoryPath != null) {
      log.info("loading last used directory from config:\t{}", currentDirectoryPath);
      runLoadDirectoryTask(Path.of(currentDirectoryPath));
    }
  }

  @Subscribe
  public void processLoadDirectoryEvent(LoadDirectoryEvent event) throws IOException {
    runLoadDirectoryTask(event.getDirectoryPath());
  }

  @Subscribe
  public void processReLoadCurrentDirectoryEvent(ReLoadCurrentDirectoryEvent event) {
    if (currentDirectoryPath != null) {
      runLoadDirectoryTask(currentDirectoryPath);
    }
  }

  @Subscribe
  public void processClearFileSelectionEvent(ClearFileSelectionEvent event) {
    filteredFileList.setPredicate(x -> true);
    this.fileListView.getSelectionModel().clearSelection();
  }

  @Subscribe
  public void processAutoCompleteSelectionChangedEvent(AutoCompleteSelectionChangedEvent event) {
    filteredFileList.setPredicate(path -> path.getFileName().toString().toLowerCase()
        .contains(event.getSelection().toLowerCase()));
    // fileListView.getSelectionModel().select(0);
  }

  /////
  /////
  /////

  private void initFileListView() {
    observableFileList = FXCollections.observableArrayList();
    filteredFileList = new FilteredList<>(observableFileList);
    fileListView.setItems(filteredFileList);

    fileListView.setCellFactory(new FileListViewCellFactory());
    fileListView.getSelectionModel().selectedItemProperty()
        .addListener((obs, oldValue, newValue) -> {
          eventBusFx.notify(new FileSelectionChangedEvent(newValue));
        });
  }

  private void runLoadDirectoryTask(Path directoryPath) {
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

      eventBusFx.notify(new FilesListChangedEvent(task.getValue()));

      currentDirectoryPath = directoryPath;
      pdfViewerConfig.saveLastUsedDirectory(directoryPath.toString());
      eventBusFx.notify(new DirectoryChangedEvent(directoryPath));

      eventBusFx.notify(new TaskDoneEvent());
    });
    task.setOnFailed(e -> {
      log.info("loading directory failed");
      eventBusFx.notify(new TaskDoneEvent());
    });

    executor.submit(task);
  }

}
