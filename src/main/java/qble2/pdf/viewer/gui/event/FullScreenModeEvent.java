package qble2.pdf.viewer.gui.event;

import lombok.Getter;

@Getter
public class FullScreenModeEvent {

  private boolean isFullScreen;

  public FullScreenModeEvent(boolean isFullScreen) {
    this.isFullScreen = isFullScreen;
  }

}
