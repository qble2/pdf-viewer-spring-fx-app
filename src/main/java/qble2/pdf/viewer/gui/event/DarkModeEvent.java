package qble2.pdf.viewer.gui.event;

import lombok.Getter;

@Getter
public class DarkModeEvent {

  private boolean isDarkMode;

  public DarkModeEvent(boolean isDarkMode) {
    this.isDarkMode = isDarkMode;
  }

}
