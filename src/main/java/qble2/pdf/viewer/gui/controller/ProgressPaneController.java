package qble2.pdf.viewer.gui.controller;

import java.net.URL;
import java.util.EventListener;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.TaskDoneEvent;
import qble2.pdf.viewer.gui.event.TaskRunningEvent;

@Component
public class ProgressPaneController implements Initializable, EventListener {

  @Autowired
  private EventBusFx eventBusFx;

  @FXML
  private BorderPane progressPane;

  @FXML
  private ProgressIndicator progressIndicator;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);

    progressPane.setVisible(false);
    progressPane.setStyle("-fx-background-color: rgba(159, 159, 159, 0.5);");

    // indeterminate, since tasks will not provide incremental updates
    progressIndicator.setProgress(-1d);
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processTaskRunningEvent(TaskRunningEvent event) {
    progressPane.setVisible(true);
    progressPane.getScene().setCursor(Cursor.WAIT);
    // progressIndicator.progressProperty().bind(event.getTask().progressProperty());
    // progressPane.visibleProperty().bind(event.getTask().workDoneProperty().lessThan(1d));
  }

  @Subscribe
  public void processTaskDoneEvent(TaskDoneEvent event) {
    // progressIndicator.progressProperty().unbind();
    progressPane.setVisible(false);
    progressPane.getScene().setCursor(Cursor.DEFAULT);
  }

  /////
  /////
  /////

}
