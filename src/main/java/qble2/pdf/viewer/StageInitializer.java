package qble2.pdf.viewer;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import qble2.pdf.viewer.gui.ViewConstant;
import qble2.pdf.viewer.gui.controller.MainController;

@Component
@Slf4j
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

  @Value("classpath:/fxml/main.fxml")
  private Resource mainFxmlResource;

  private ApplicationContext applicationContext;

  public StageInitializer(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public void onApplicationEvent(StageReadyEvent event) {
    try {
      Stage stage = event.getStage();
      stage.setTitle(ViewConstant.APP_TITLE);

      FXMLLoader fxmlLoader = new FXMLLoader(mainFxmlResource.getURL());
      fxmlLoader.setControllerFactory(applicationContext::getBean); // !
      Parent parent = fxmlLoader.load();
      MainController mainController = fxmlLoader.<MainController>getController();
      mainController.setStage(stage);

      Scene scene = new Scene(parent);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

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
