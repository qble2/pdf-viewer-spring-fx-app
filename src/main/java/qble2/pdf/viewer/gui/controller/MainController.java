package qble2.pdf.viewer.gui.controller;

import java.net.URL;
import java.util.EventListener;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import qble2.pdf.viewer.StageInitializer;
import qble2.pdf.viewer.gui.PdfViewerConfig;
import qble2.pdf.viewer.gui.event.AppColorChangedEvent;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.FileSelectionChangedEvent;
import qble2.pdf.viewer.gui.event.RequestFullScreenModeEvent;

@Component
@Slf4j
public class MainController implements Initializable, EventListener {

  @Autowired
  private EventBusFx eventBusFx;

  @Autowired
  private PdfViewerConfig pdfViewerConfig;

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
    String fileName = StageInitializer.APP_TITLE;
    if (event.getFilePath() != null) {
      fileName = event.getFilePath().getFileName().toString();
    }
    this.stage.setTitle(fileName);
  }

  @Subscribe
  public void processRequestFullScreenModeEvent(RequestFullScreenModeEvent event) {
    this.stage.setFullScreen(true);
  }

  @Subscribe
  public void processAppColorChangedEvent(AppColorChangedEvent event) {
    Color color = event.getColor();
    String hexColor = toHex(color);
    log.debug("changing app-color to: {} , hex: {}", color, hexColor);

    this.stage.getScene().getRoot().setStyle(String.format("-app-color: %s;", hexColor));
    pdfViewerConfig.setAppColor(hexColor);
  }

  /////
  /////
  /////

  private static String toHex(Color color) {
    int r = ((int) Math.round(color.getRed() * 255)) << 24;
    int g = ((int) Math.round(color.getGreen() * 255)) << 16;
    int b = ((int) Math.round(color.getBlue() * 255)) << 8;
    int a = ((int) Math.round(color.getOpacity() * 255));
    return String.format("#%08X", (r + g + b + a));
  }

}
