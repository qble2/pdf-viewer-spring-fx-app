package qble2.pdf.viewer;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import javafx.application.Application;

@SpringBootApplication
@EnableAsync
public class PdfViewerSpringApplication {

  public static void main(String[] args) {
    Application.launch(PdfViewerFxApplication.class, args);
  }
}
