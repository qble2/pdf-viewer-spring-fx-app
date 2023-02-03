package qble2.pdf.viewer.gui.controller;

import java.net.URL;
import java.util.EventListener;
import java.util.ResourceBundle;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import qble2.pdf.viewer.gui.PdfViewerConfig;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.FileSelectionChangedEvent;
import qble2.pdf.viewer.gui.event.FullScreenModeEvent;

@Component
public class FooterController implements Initializable, EventListener {

  @Autowired
  private EventBusFx eventBusFx;

  @Autowired
  private PdfViewerConfig pdfViewerConfig;

  @FXML
  private Parent root;

  @FXML
  private Label fileNameLabel;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);

    root.managedProperty().bind(root.visibleProperty());
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

  @Subscribe
  public void processFullScreenModeEvent(FullScreenModeEvent event) {
    boolean isVisible = !event.isFullScreen()
        || (event.isFullScreen() && pdfViewerConfig.isFooterVisibleInFullScreenModeCheckbox());
    this.root.setVisible(isVisible);
  }

  /////
  /////
  /////

}
