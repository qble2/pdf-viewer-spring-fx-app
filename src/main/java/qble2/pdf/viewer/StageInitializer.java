package qble2.pdf.viewer;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
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
import qble2.pdf.viewer.gui.controller.SplitPdfDialogController;
import qble2.pdf.viewer.gui.event.FullScreenModeEvent;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.StageShownEvent;

@Component
@Slf4j
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

  @Autowired
  private EventBusFx eventBusFx;

  @Value("classpath:/fxml/main.fxml")
  private Resource mainFxmlResource;

  @Value("classpath:/fxml/splitPdfDialog.fxml")
  private Resource splitPdfDialogFxmlResource;

  private ApplicationContext applicationContext;

  public StageInitializer(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public void onApplicationEvent(StageReadyEvent event) {
    try {
      Stage stage = event.getStage();
      stage.setTitle(ViewConstant.APP_TITLE);

      // main view
      FXMLLoader fxmlLoader = new FXMLLoader(mainFxmlResource.getURL());
      fxmlLoader.setControllerFactory(applicationContext::getBean); // !
      Parent parent = fxmlLoader.load();
      MainController mainController = fxmlLoader.<MainController>getController();
      mainController.setStage(stage);

      // split PDF file dialog
      FXMLLoader dialogFxmlLoader = new FXMLLoader(splitPdfDialogFxmlResource.getURL());
      dialogFxmlLoader.setControllerFactory(applicationContext::getBean); // !
      dialogFxmlLoader.load();
      SplitPdfDialogController splitPdfDialogController =
          dialogFxmlLoader.<SplitPdfDialogController>getController();
      splitPdfDialogController.setStage(stage);

      Scene scene = new Scene(parent);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
      stage.setScene(scene);

      stage.fullScreenProperty().addListener((obs, oldValue, newValue) -> {
        eventBusFx.notify(new FullScreenModeEvent(newValue));
      });

      stage.setOnShown(e -> {
        eventBusFx.notify(new StageShownEvent());
      });

      stage.show();
      stage.toFront();

      log.info("FX application has started.");
    } catch (IOException e) {
      log.error("an error has occured", e);
      throw new RuntimeException(e);
    }
  }

}
