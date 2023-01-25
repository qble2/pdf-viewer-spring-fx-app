package qble2.document.viewer;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import javafx.application.Application;

@SpringBootApplication
@EnableAsync
public class DocumentViewerSpringApplication {

  public static void main(String[] args) {
    Application.launch(DocumentViewerFxApplication.class, args);
  }
}
