package qble2.pdf.viewer.gui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PdfViewerConfig extends Configurations {

  private static final Path CONFIG_FILE_PATH = Paths.get("./settings.properties");

  private static final String FULL_SCREEN_MODE_FILE_LIST_VISIBLE =
      "fullScreenMode.fileList.visible";
  private static final String FULL_SCREEN_MODE_PDF_VIEW_THUMBNAILS_VISIBLE =
      "fullScreenMode.pdfView.thumbnails.visible";
  private static final String FULL_SCREEN_MODE_PDF_VIEW_TOOL_BAR_VISIBLE =
      "fullScreenMode.pdfView.toolBar.visible";
  private static final String FULL_SCREEN_MODE_FOOTER_VISIBLE = "fullScreenMode.footer.visible";

  private static final String PDF_VIEW_TOOL_BAR_DEFAULT_SIZE = "pdfView.thumbnails.size";
  private static final String FULL_SCREEN_MODE_PDF_VIEW_TOOL_BAR_SIZE =
      "fullScreenMode.pdfView.thumbnails.size";

  private Configuration config;
  private FileBasedConfigurationBuilder<PropertiesConfiguration> builder;

  public PdfViewerConfig() {
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

  public boolean isFileListVisibleInFullScreenMode() {
    return this.config.getBoolean(FULL_SCREEN_MODE_FILE_LIST_VISIBLE, false);
  }

  public void setFileListVisibleInFullScreenMode(boolean isFileListVisibleInFullScreenMode) {
    this.config.setProperty(FULL_SCREEN_MODE_FILE_LIST_VISIBLE, isFileListVisibleInFullScreenMode);
  }

  public boolean isPdfThumbnailsVisibleInFullScreenMode() {
    return this.config.getBoolean(FULL_SCREEN_MODE_PDF_VIEW_THUMBNAILS_VISIBLE, false);
  }

  public void setPdfThumbnailsVisibleInFullScreenMode(
      boolean isPdfViewThumbnailsVisibleInFullScreenMode) {
    this.config.setProperty(FULL_SCREEN_MODE_PDF_VIEW_THUMBNAILS_VISIBLE,
        isPdfViewThumbnailsVisibleInFullScreenMode);
  }

  public boolean isPdfViewToolBarVisibleInFullScreenModeCheckbox() {
    return this.config.getBoolean(FULL_SCREEN_MODE_PDF_VIEW_TOOL_BAR_VISIBLE, false);
  }

  public void setPdfViewToolBarVisibleInFullScreenMode(
      boolean isPdfToolBarVisibleInFullScreenMode) {
    this.config.setProperty(FULL_SCREEN_MODE_PDF_VIEW_TOOL_BAR_VISIBLE,
        isPdfToolBarVisibleInFullScreenMode);
  }

  public boolean isFooterVisibleInFullScreenModeCheckbox() {
    return this.config.getBoolean(FULL_SCREEN_MODE_FOOTER_VISIBLE, false);
  }

  public void setFooterVisibleInFullScreenMode(boolean isFooterVisibleInFullScreenModeCheckbox) {
    this.config.setProperty(FULL_SCREEN_MODE_FOOTER_VISIBLE,
        isFooterVisibleInFullScreenModeCheckbox);
  }

  public int getPdfViewThumbnailsDefaultSize() {
    return this.config.getInt(PDF_VIEW_TOOL_BAR_DEFAULT_SIZE, 200);
  }

  public void setPdfViewThumbnailsDefaultSize(int pdfViewThumbnailsDefaultSize) {
    this.config.setProperty(PDF_VIEW_TOOL_BAR_DEFAULT_SIZE, pdfViewThumbnailsDefaultSize);
  }

  public int getPdfViewThumbnailsSizeInFullScreenMode() {
    return this.config.getInt(FULL_SCREEN_MODE_PDF_VIEW_TOOL_BAR_SIZE, 200);
  }

  public void setPdfViewThumbnailsSizeInFullScreenMode(int pdfViewThumbnailsSizeInFullScreenMode) {
    this.config.setProperty(FULL_SCREEN_MODE_PDF_VIEW_TOOL_BAR_SIZE,
        pdfViewThumbnailsSizeInFullScreenMode);
  }

}
