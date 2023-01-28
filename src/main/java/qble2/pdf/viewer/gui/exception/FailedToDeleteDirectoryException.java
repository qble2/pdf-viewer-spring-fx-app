package qble2.pdf.viewer.gui.exception;

public class FailedToDeleteDirectoryException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public FailedToDeleteDirectoryException(String message) {
    super(message);
  }

}
