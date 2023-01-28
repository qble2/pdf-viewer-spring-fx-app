package qble2.pdf.viewer.gui.exception;

public class FailedToLoadPdfFileException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public FailedToLoadPdfFileException(String message) {
    super(message);
  }

}
