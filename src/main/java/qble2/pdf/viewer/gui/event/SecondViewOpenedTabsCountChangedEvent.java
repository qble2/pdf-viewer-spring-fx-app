package qble2.pdf.viewer.gui.event;

import lombok.Getter;

@Getter
public class SecondViewOpenedTabsCountChangedEvent {

  private int opendedTabsCount;

  public SecondViewOpenedTabsCountChangedEvent(int opendedTabsCount) {
    this.opendedTabsCount = opendedTabsCount;
  }

}
