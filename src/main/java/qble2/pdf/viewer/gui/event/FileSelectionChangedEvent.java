package qble2.pdf.viewer.gui.event;

import java.nio.file.Path;
import lombok.Getter;

@Getter
public class FileSelectionChangedEvent {

  private Path filePath;

  public FileSelectionChangedEvent(Path filePath) {
    this.filePath = filePath;
  }

}
