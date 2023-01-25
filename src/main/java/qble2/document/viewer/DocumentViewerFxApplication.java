package qble2.document.viewer;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import qble2.document.viewer.gui.PdfViewerConfig;

@Slf4j
public class DocumentViewerFxApplication extends Application {

  private ConfigurableApplicationContext applicationContext;

  @Override
  public void init() throws Exception {
    ApplicationContextInitializer<GenericApplicationContext> initializer = c -> {
      c.registerBean(Application.class, () -> DocumentViewerFxApplication.this);
      c.registerBean(Parameters.class, this::getParameters);
      c.registerBean(HostServices.class, this::getHostServices);
    };

    applicationContext = new SpringApplicationBuilder(DocumentViewerSpringApplication.class)
        .initializers(initializer).run(getParameters().getRaw().toArray(new String[0]));
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    log.info("FX application is starting...");
    applicationContext.publishEvent(new StageReadyEvent(primaryStage));
  }

  @Override
  public void stop() throws Exception {
    log.info("FX application is stopping");

    PdfViewerConfig.getInstance().saveConfig();

    applicationContext.close();
    Platform.exit();
  }

}


class StageReadyEvent extends ApplicationEvent {

  private static final long serialVersionUID = 1L;

  public StageReadyEvent(Stage stage) {
    super(stage);
  }

  public Stage getStage() {
    return ((Stage) getSource());
  }

}
