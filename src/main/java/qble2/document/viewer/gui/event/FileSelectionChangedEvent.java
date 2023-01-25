package qble2.document.viewer.gui.event;

import java.nio.file.Path;
import lombok.Getter;

@Getter
public class FileSelectionChangedEvent {

  private Path filePath;

  public FileSelectionChangedEvent(Path filePath) {
    this.filePath = filePath;
  }

}
