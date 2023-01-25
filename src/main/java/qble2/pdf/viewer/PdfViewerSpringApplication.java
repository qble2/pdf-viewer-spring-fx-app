package qble2.pdf.viewer;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import javafx.application.Application;

@SpringBootApplication
public class PdfViewerSpringApplication {

  public static void main(String[] args) {
    Application.launch(PdfViewerFxApplication.class, args);
  }
}
