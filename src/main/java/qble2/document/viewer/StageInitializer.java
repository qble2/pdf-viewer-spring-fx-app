package qble2.document.viewer;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

  private static final String APP_TITLE = "Document Viewer FX";

  @Value("classpath:/fxml/main.fxml")
  private Resource mainFxmlResource;

  // private ApplicationContext applicationContext;

  // public StageInitializer(ApplicationContext applicationContext) {
  // this.applicationContext = applicationContext;
  // }

  @Override
  public void onApplicationEvent(StageReadyEvent event) {
    try {
      Stage stage = event.getStage();
      stage.setTitle(APP_TITLE);
      stage.setWidth(500d);
      stage.setHeight(500d);

      FXMLLoader fxmlLoader = new FXMLLoader(mainFxmlResource.getURL());
      // fxmlLoader.setControllerFactory(applicationContext::getBean);
      Parent parent = fxmlLoader.load();
      Scene scene = new Scene(parent);
      stage.setScene(scene);

      stage.show();
      stage.toFront();

      log.info("FX application has started.");
    } catch (IOException e) {
      log.error("an error has occured", e);
      throw new RuntimeException(e);
    }
  }

}
