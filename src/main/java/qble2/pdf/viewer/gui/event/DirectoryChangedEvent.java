package qble2.pdf.viewer.gui.event;

import java.nio.file.Path;
import lombok.Getter;

@Getter
public class DirectoryChangedEvent {

  private Path directoryPath;

  public DirectoryChangedEvent(Path directoryPath) {
    this.directoryPath = directoryPath;
  }

}
