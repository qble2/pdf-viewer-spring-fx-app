package qble2.pdf.viewer.gui.event;

import lombok.Getter;

@Getter
public class PageSelectionChangedEvent {

  private int page;

  public PageSelectionChangedEvent(int page) {
    this.page = page;
  }

}
