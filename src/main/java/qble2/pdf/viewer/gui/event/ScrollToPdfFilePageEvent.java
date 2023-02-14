package qble2.pdf.viewer.gui.event;

import lombok.Getter;

@Getter
public class ScrollToPdfFilePageEvent {

  private int page;

  public ScrollToPdfFilePageEvent(int page) {
    this.page = page;
  }

}
