package qble2.pdf.viewer;

import org.springframework.beans.factory.annotation.Autowired;
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
import qble2.pdf.viewer.gui.PdfViewerConfig;

@Slf4j
public class PdfViewerFxApplication extends Application {

  @Autowired
  private PdfViewerConfig pdfViewerConfig;

  private ConfigurableApplicationContext applicationContext;

  @Override
  public void init() throws Exception {
    ApplicationContextInitializer<GenericApplicationContext> initializer = c -> {
      c.registerBean(Application.class, () -> PdfViewerFxApplication.this);
      c.registerBean(Parameters.class, this::getParameters);
      c.registerBean(HostServices.class, this::getHostServices);
    };

    applicationContext = new SpringApplicationBuilder(PdfViewerSpringApplication.class)
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

    pdfViewerConfig.saveConfig();

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
