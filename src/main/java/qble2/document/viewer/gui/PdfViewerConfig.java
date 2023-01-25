package qble2.document.viewer.gui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PdfViewerConfig extends Configurations {

  private static final Path CONFIG_FILE_PATH = Paths.get("./settings.properties");

  private Configuration config;
  private FileBasedConfigurationBuilder<PropertiesConfiguration> builder;

  private static final PdfViewerConfig INSTANCE = new PdfViewerConfig();

  private PdfViewerConfig() {
    try {
      if (Files.notExists(CONFIG_FILE_PATH)) {
        Files.createFile(CONFIG_FILE_PATH);
      }

      log.info("loading configuration file: {}", CONFIG_FILE_PATH.getFileName());
      this.builder =
          this.fileBasedBuilder(PropertiesConfiguration.class, CONFIG_FILE_PATH.toFile());
      builder.setAutoSave(false);

      config = builder.getConfiguration();
    } catch (IOException | ConfigurationException e) {
      log.error("an error has occured", e);
    }
  }

  public static PdfViewerConfig getInstance() {
    return INSTANCE;
  }

  public void saveConfig() {
    try {
      log.info("saving configuration file: {}", CONFIG_FILE_PATH.getFileName());
      this.builder.save();
    } catch (ConfigurationException e) {
      log.error("an error has occured", e);
    }
  }

  public void saveLastUsedDirectory(String directory) {
    this.config.setProperty("directory", directory);
  }

  public String getLastUsedDirectory() {
    return this.config.getString("directory");
  }

}
