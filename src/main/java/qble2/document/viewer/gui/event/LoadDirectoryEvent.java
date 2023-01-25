package qble2.document.viewer.gui.event;

import java.nio.file.Path;
import lombok.Getter;

@Getter
public class LoadDirectoryEvent {

  private Path directoryPath;

  public LoadDirectoryEvent(Path directoryPath) {
    this.directoryPath = directoryPath;
  }

}
