package qble2.pdf.viewer.gui.event;

import java.nio.file.Path;
import lombok.Getter;

@Getter
public class SplitPdfFileEvent {

  private Path filePath;

  public SplitPdfFileEvent(Path filePath) {
    this.filePath = filePath;
  }

}
