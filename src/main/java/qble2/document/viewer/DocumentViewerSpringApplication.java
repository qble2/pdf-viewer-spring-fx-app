package qble2.document.viewer;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import javafx.application.Application;

@SpringBootApplication
public class DocumentViewerSpringApplication {

  public static void main(String[] args) {
    Application.launch(DocumentViewerFxApplication.class, args);
  }
}
