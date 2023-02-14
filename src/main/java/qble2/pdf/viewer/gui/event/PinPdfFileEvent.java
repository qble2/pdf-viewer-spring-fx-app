package qble2.pdf.viewer.gui.event;

import java.nio.file.Path;
import lombok.Getter;

@Getter
public class PinPdfFileEvent {

  private Path filePath;

  public PinPdfFileEvent(Path filePath) {
    this.filePath = filePath;
  }

}
