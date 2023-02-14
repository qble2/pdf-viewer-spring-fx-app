package qble2.pdf.viewer.gui.event;

import lombok.Getter;

@Getter
public class SecondViewOpenedTabsCountChangedEvent {

  private int openedTabsCount;

  public SecondViewOpenedTabsCountChangedEvent(int openedTabsCount) {
    this.openedTabsCount = openedTabsCount;
  }

}
