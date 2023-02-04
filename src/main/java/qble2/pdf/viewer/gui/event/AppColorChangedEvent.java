package qble2.pdf.viewer.gui.event;

import javafx.scene.paint.Color;
import lombok.Getter;

@Getter
public class AppColorChangedEvent {

  private Color color;

  public AppColorChangedEvent(Color color) {
    this.color = color;
  }

}
