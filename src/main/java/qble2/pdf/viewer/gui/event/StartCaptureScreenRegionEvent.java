package qble2.pdf.viewer.gui.event;

import java.nio.file.Path;
import lombok.Getter;

@Getter
public class StartCaptureScreenRegionEvent {

  private Path filePath;

  /**
   * 0-based
   */
  private int filePage;

  public StartCaptureScreenRegionEvent(Path filePath, int filePage) {
    this.filePath = filePath;
    this.filePage = filePage;
  }

}
