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
import qble2.pdf.viewer.gui.controller.MainViewController;
import qble2.pdf.viewer.gui.controller.SettingsDialogViewController;
import qble2.pdf.viewer.gui.controller.SplitPdfDialogViewController;
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

  @Value("classpath:/fxml/settingsDialogPane.fxml")
  private Resource settingsDialogPaneFxmlResource;

  @Value("classpath:/fxml/splitPdfDialogPane.fxml")
  private Resource splitPdfDialogPaneFxmlResource;

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
      MainViewController mainController = fxmlLoader.<MainViewController>getController();
      mainController.setStage(stage);

      // config dialog
      FXMLLoader settingsDialogFxmlLoader = new FXMLLoader(settingsDialogPaneFxmlResource.getURL());
      settingsDialogFxmlLoader.setControllerFactory(applicationContext::getBean); // !
      settingsDialogFxmlLoader.load();
      SettingsDialogViewController settingsDialogPaneController =
          settingsDialogFxmlLoader.<SettingsDialogViewController>getController();
      settingsDialogPaneController.setStage(stage);

      // split PDF dialog
      FXMLLoader splitPdfDialogFxmlLoader = new FXMLLoader(splitPdfDialogPaneFxmlResource.getURL());
      splitPdfDialogFxmlLoader.setControllerFactory(applicationContext::getBean); // !
      splitPdfDialogFxmlLoader.load();
      SplitPdfDialogViewController splitPdfDialogController =
          splitPdfDialogFxmlLoader.<SplitPdfDialogViewController>getController();
      splitPdfDialogController.setStage(stage);

      Scene scene = new Scene(parent, 1360d, 768d);
      stage.setScene(scene);

      // load settings
      stage.setMaximized(pdfViewerConfig.isMaximizeStageAtStartup());
      scene.getStylesheets()
          .add(getClass()
              .getResource(pdfViewerConfig.isDarkModeEnabled() ? "/css/dark.css" : "/css/light.css")
              .toExternalForm());

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
      log.error("An error has occured", e);
      throw new RuntimeException(e);
    }
  }

}
