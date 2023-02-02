package qble2.pdf.viewer.gui.controller;

import java.net.URL;
import java.util.EventListener;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.Setter;
import qble2.pdf.viewer.gui.ViewConstant;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.FileSelectionChangedEvent;
import qble2.pdf.viewer.gui.event.RequestFullScreenModeEvent;

@Component
public class MainController implements Initializable, EventListener {

  @Autowired
  private EventBusFx eventBusFx;

  //
  @Setter
  private Stage stage;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processFileSelectionChangedEvent(FileSelectionChangedEvent event) {
    String fileName = ViewConstant.APP_TITLE;
    if (event.getFilePath() != null) {
      fileName = event.getFilePath().getFileName().toString();
    }
    this.stage.setTitle(fileName);
  }

  @Subscribe
  public void processRequestFullScreenModeEvent(RequestFullScreenModeEvent event) {
    this.stage.setFullScreen(true);
  }

  /////
  /////
  /////

}
