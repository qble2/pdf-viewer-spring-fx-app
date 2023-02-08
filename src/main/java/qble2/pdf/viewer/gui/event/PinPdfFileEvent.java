package qble2.pdf.viewer.gui.event;

import java.nio.file.Path;
import lombok.Getter;

@Getter
public class PinPdfFileEvent {

  private Path pdfFilePath;

  public PinPdfFileEvent(Path pdfFilePath) {
    this.pdfFilePath = pdfFilePath;
  }

}
