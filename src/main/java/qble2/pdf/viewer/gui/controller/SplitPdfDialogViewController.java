package qble2.pdf.viewer.gui.controller;

import java.net.URL;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.LoadDirectoryEvent;
import qble2.pdf.viewer.gui.event.SplitPdfFileEvent;
import qble2.pdf.viewer.gui.event.StageShownEvent;
import qble2.pdf.viewer.system.SplitPdfFileService;

@Component
@Slf4j
public class SplitPdfDialogViewController implements Initializable, EventListener {

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
  private SplitPdfFileService splitPdfFileService;

  @FXML
  private DialogPane dialogPane;

  @FXML
  private Label pdfFileName;

  @FXML
  private Label totalNumberOfPagesLabel;

  @FXML
  private ProgressBar progressBar;

  @FXML
  private Label currentOperationLabel;

  @FXML
  private Label workDonePercentageLabel;

  @FXML
  private Label currentEntryLabel;

  @FXML
  private Label totalNumberOfSplitFilesCreatedLabel;

  @FXML
  private Label splitFilesTargetDirectoryLabel;

  @FXML
  private Label errorMessageLabel;

  //
  @Setter
  private Stage stage;
  private Dialog<?> dialog;
  private Task<Path> task;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);

    dialog = new Dialog<>();
    dialog.setDialogPane(dialogPane);

    dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

    errorMessageLabel.managedProperty().bind(errorMessageLabel.visibleProperty());
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processStageShownEvent(StageShownEvent event) {
    this.dialog.initOwner(stage);
  }

  @Subscribe
  public void processSplitPdfFileEvent(SplitPdfFileEvent event) {
    if (task == null || !task.isRunning()) {
      cleanState();
      runSplitPdfFileTask(event.getFilePath());
    }

    dialog.show();
  }

  /////
  /////
  /////

  private void cleanState() {
    dialog.setTitle(null);

    progressBar.progressProperty().unbind();
    progressBar.setProgress(-1);
    progressBar.setStyle(null);

    pdfFileName.setText(null);
    totalNumberOfPagesLabel.setText(null);
    currentOperationLabel.setText(null);
    workDonePercentageLabel.setText(null);
    currentEntryLabel.setText(null);
    totalNumberOfSplitFilesCreatedLabel.setText(null);

    errorMessageLabel.setVisible(false);
    errorMessageLabel.setText(null);
  }

  private void runSplitPdfFileTask(Path pdfFilePath) {
    pdfFileName.setText(pdfFilePath.getFileName().toString());

    task = new Task<>() {
      @Override
      protected Path call() throws Exception {
        splitPdfFileService.setSplitFilesTargetDirectoryConsumer(s -> {
          Platform.runLater(() -> splitFilesTargetDirectoryLabel.setText(s));
        });
        splitPdfFileService.setCurrentOperationConsumer(s -> {
          Platform.runLater(() -> currentOperationLabel.setText(s));
        });
        splitPdfFileService.setTotalNumberOfPagesConsumer(i -> {
          Platform.runLater(() -> totalNumberOfPagesLabel.setText(String.valueOf(i)));
        });
        splitPdfFileService.setCurrentEntryConsumer(s -> {
          Platform.runLater(() -> currentEntryLabel.setText(s));
        });
        splitPdfFileService.setProgressUpdateConsumer((workDone, max) -> {
          Platform.runLater(() -> {
            int workDonePercentage = workDone * 100 / max;
            dialog.setTitle(workDonePercentage + "% done");
            workDonePercentageLabel.setText(workDonePercentage + "% done");
            updateProgress(workDone, max);
          });
        });
        splitPdfFileService.setTotalNumberOfSplitFilesCreatedConsumer(i -> {
          Platform.runLater(() -> totalNumberOfSplitFilesCreatedLabel.setText(String.valueOf(i)));
        });

        return splitPdfFileService.splitPdfFile(pdfFilePath);
      }
    };
    task.exceptionProperty().addListener((obs, oldValue, newValue) -> {
      if (newValue != null) {
        log.error("An error has occurred", newValue);
        errorMessageLabel.setText(newValue.getMessage());
        errorMessageLabel.setVisible(true);
      }
    });
    task.setOnSucceeded(e -> {
      log.info("PDF file has been successfully split");
      progressBar.progressProperty().unbind();
      eventBusFx.notify(new LoadDirectoryEvent(task.getValue()));
    });
    task.setOnFailed(e -> {
      log.info("Failed to split PDF file");
      dialog.setTitle("Failed");
      currentOperationLabel.setText(currentOperationLabel.getText() + " (failed)");
      progressBar.progressProperty().unbind();
      progressBar.setStyle("-fx-accent: red;");
    });

    progressBar.progressProperty().bind(task.progressProperty());
    executor.submit(task);
  }

}
