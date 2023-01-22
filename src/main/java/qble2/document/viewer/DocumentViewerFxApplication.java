package qble2.document.viewer;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentViewerFxApplication extends Application {

  private ConfigurableApplicationContext applicationContext;

  @Override
  public void init() throws Exception {
    applicationContext = new SpringApplicationBuilder(DocumentViewerSpringApplication.class).run();

    // ApplicationContextInitializer<GenericApplicationContext> initializer = c -> {
    // c.registerBean(Parameters.class, this::getParameters);
    // c.registerBean(HostServices.class, this::getHostServices);
    // };
    //
    // applicationContext = new SpringApplicationBuilder(DocumentViewerSpringApplication.class)
    // .initializers(initializer).run();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    log.info("FX application is starting...");
    applicationContext.publishEvent(new StageReadyEvent(primaryStage));
  }

  @Override
  public void stop() throws Exception {
    log.info("FX application is stopping");
    applicationContext.close();
    Platform.exit();
  }

}
