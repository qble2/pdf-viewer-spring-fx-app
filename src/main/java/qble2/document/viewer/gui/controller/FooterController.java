package qble2.document.viewer.gui.controller;

import java.net.URL;
import java.util.EventListener;
import java.util.ResourceBundle;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import qble2.document.viewer.gui.event.EventBusFx;
import qble2.document.viewer.gui.event.FileSelectionChangedEvent;

@Component
public class FooterController implements Initializable, EventListener {

  @FXML
  private Label fileNameLabel;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    EventBusFx.getInstance().registerListener(this);
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processFileSelectionChangedEvent(FileSelectionChangedEvent event) {
    String fileName = StringUtils.EMPTY;
    if (event.getFilePath() != null) {
      fileName = event.getFilePath().getFileName().toString();
    }
    this.fileNameLabel.setText(fileName);
  }

  /////
  /////
  /////

}
