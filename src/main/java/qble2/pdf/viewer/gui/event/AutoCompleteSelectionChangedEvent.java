package qble2.pdf.viewer.gui.event;

import lombok.Getter;

@Getter
public class AutoCompleteSelectionChangedEvent {

  private String selection;

  public AutoCompleteSelectionChangedEvent(String selection) {
    this.selection = selection;
  }

}
