package qble2.pdf.viewer.gui.event;

import javafx.scene.Node;
import lombok.Getter;
import qble2.pdf.viewer.business.FileNote;

@Getter
public class DeleteFileNoteEvent {

  private FileNote fileNote;
  private Node node;

  public DeleteFileNoteEvent(FileNote fileNote, Node node) {
    this.fileNote = fileNote;
    this.node = node;
  }

}
