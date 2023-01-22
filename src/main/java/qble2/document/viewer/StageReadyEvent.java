package qble2.document.viewer;

import org.springframework.context.ApplicationEvent;
import javafx.stage.Stage;

public class StageReadyEvent extends ApplicationEvent {

  private static final long serialVersionUID = 1L;

  public StageReadyEvent(Stage stage) {
    super(stage);
  }

  public Stage getStage() {
    return ((Stage) getSource());
  }

}
