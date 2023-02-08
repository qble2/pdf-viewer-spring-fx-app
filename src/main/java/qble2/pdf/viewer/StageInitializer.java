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
import qble2.pdf.viewer.gui.PdfViewerConfig;
import qble2.pdf.viewer.gui.controller.MainController;
import qble2.pdf.viewer.gui.controller.SettingsDialogController;
import qble2.pdf.viewer.gui.controller.SplitPdfDialogController;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.FullScreenModeEvent;
import qble2.pdf.viewer.gui.event.StageShownEvent;

@Component
@Slf4j
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

  public static final String APP_TITLE = "PDF Viewer Spring FX";

  @Autowired
  private EventBusFx eventBusFx;

  @Autowired
  private PdfViewerConfig pdfViewerConfig;

  @Value("classpath:/fxml/main.fxml")
  private Resource mainFxmlResource;

  @Value("classpath:/fxml/settingsDialog.fxml")
  private Resource configDialogFxmlResource;

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
      stage.setTitle(APP_TITLE);

      // main view
      FXMLLoader fxmlLoader = new FXMLLoader(mainFxmlResource.getURL());
      fxmlLoader.setControllerFactory(applicationContext::getBean); // !
      Parent parent = fxmlLoader.load();
      MainController mainController = fxmlLoader.<MainController>getController();
      mainController.setStage(stage);

      // config dialog
      FXMLLoader configDialogFxmlLoader = new FXMLLoader(configDialogFxmlResource.getURL());
      configDialogFxmlLoader.setControllerFactory(applicationContext::getBean); // !
      configDialogFxmlLoader.load();
      SettingsDialogController configDialogPaneController =
          configDialogFxmlLoader.<SettingsDialogController>getController();
      configDialogPaneController.setStage(stage);

      // split PDF dialog
      FXMLLoader splitPdfDialogFxmlLoader = new FXMLLoader(splitPdfDialogFxmlResource.getURL());
      splitPdfDialogFxmlLoader.setControllerFactory(applicationContext::getBean); // !
      splitPdfDialogFxmlLoader.load();
      SplitPdfDialogController splitPdfDialogController =
          splitPdfDialogFxmlLoader.<SplitPdfDialogController>getController();
      splitPdfDialogController.setStage(stage);

      Scene scene = new Scene(parent);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
      stage.setScene(scene);

      // load settings
      stage.setMaximized(pdfViewerConfig.isMaximizeStageAtStartup());
      stage.getScene().getRoot()
          .setStyle(String.format("-app-color: %s;", pdfViewerConfig.getAppColor()));

      stage.fullScreenProperty().addListener((obs, oldValue, newValue) -> {
        eventBusFx.notify(new FullScreenModeEvent(newValue));
      });

      stage.setOnShown(e -> {
        eventBusFx.notify(new StageShownEvent());
      });

      stage.setMinWidth(1360d);
      stage.setMinHeight(768d);

      stage.show();
      stage.toFront();

      log.info("FX application has started.");
    } catch (IOException e) {
      log.error("an error has occured", e);
      throw new RuntimeException(e);
    }
  }

}
