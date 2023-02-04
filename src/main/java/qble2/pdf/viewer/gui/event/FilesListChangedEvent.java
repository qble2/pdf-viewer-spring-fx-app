package qble2.pdf.viewer.gui.event;

import java.nio.file.Path;
import java.util.List;
import lombok.Getter;

@Getter
public class FilesListChangedEvent {

  private List<Path> filesList;

  public FilesListChangedEvent(List<Path> filesList) {
    this.filesList = filesList;
  }

}
