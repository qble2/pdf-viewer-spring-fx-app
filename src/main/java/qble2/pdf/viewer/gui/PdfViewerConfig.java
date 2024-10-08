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

  private static final int PDF_VIEW_THUMBNAIL_SIZE = 200;

  private static final String STARTUP_MAXIMIZE_WINDOW = "startup.maximizeWindow";
  private static final String STARTUP_AUTOCOMPLETE_SUGGESTIONS_ENABLED =
      "startup.autoCompleteSuggestions.enabled";
  private static final String DARK_MODE_ENABLED = "darkMode.enabled";
  private static final String EXPAND_ALL_TREE_VIEW_ITEMS = "treeView.expandAll";
  private static final String PDF_VIEW_THUMBNAILS_SIZE = "pdfView.thumbnails.size";

  private static final String FULL_SCREEN_MODE_FILES_NAVIGATION_PANE_VISIBLE =
      "fullScreenMode.filesNavigationPane.visible";
  private static final String FULL_SCREEN_MODE_PDF_VIEW_THUMBNAILS_VISIBLE =
      "fullScreenMode.pdfView.thumbnails.visible";
  private static final String FULL_SCREEN_MODE_PDF_VIEW_TOOL_BAR_VISIBLE =
      "fullScreenMode.pdfView.toolBar.visible";
  private static final String FULL_SCREEN_MODE_FOOTER_VISIBLE = "fullScreenMode.footer.visible";
  private static final String FULL_SCREEN_MODE_PDF_VIEW_TOOL_BAR_SIZE =
      "fullScreenMode.pdfView.thumbnails.size";

  private Configuration config;
  private FileBasedConfigurationBuilder<PropertiesConfiguration> builder;

  public PdfViewerConfig() {
    try {
      if (Files.notExists(CONFIG_FILE_PATH)) {
        Files.createFile(CONFIG_FILE_PATH);
      }

      log.info("Loading configuration file: {}", CONFIG_FILE_PATH.getFileName());
      this.builder =
          this.fileBasedBuilder(PropertiesConfiguration.class, CONFIG_FILE_PATH.toFile());
      builder.setAutoSave(false);

      config = builder.getConfiguration();
    } catch (IOException | ConfigurationException e) {
      log.error("An error has occurred", e);
    }
  }

  public void saveConfig() {
    try {
      log.info("Saving configuration file: {}", CONFIG_FILE_PATH.getFileName());
      this.builder.save();
    } catch (ConfigurationException e) {
      log.error("An error has occurred", e);
    }
  }

  public void saveLastUsedDirectory(String directory) {
    this.config.setProperty("directory", directory);
  }

  public String getLastUsedDirectory() {
    return this.config.getString("directory");
  }

  public boolean isFilesNavigationPaneVisibleInFullScreenMode() {
    return this.config.getBoolean(FULL_SCREEN_MODE_FILES_NAVIGATION_PANE_VISIBLE, false);
  }

  public void setFilesNavigationPaneVisibleInFullScreenMode(
      boolean isFilesNavigationPaneVisibleInFullScreenMode) {
    this.config.setProperty(FULL_SCREEN_MODE_FILES_NAVIGATION_PANE_VISIBLE,
        isFilesNavigationPaneVisibleInFullScreenMode);
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

  public int getPdfViewThumbnailsSize() {
    return this.config.getInt(PDF_VIEW_THUMBNAILS_SIZE, PDF_VIEW_THUMBNAIL_SIZE);
  }

  public void setPdfViewThumbnailsSize(int pdfViewThumbnailsDefaultSize) {
    this.config.setProperty(PDF_VIEW_THUMBNAILS_SIZE, pdfViewThumbnailsDefaultSize);
  }

  public int getPdfViewThumbnailsSizeInFullScreenMode() {
    return this.config.getInt(FULL_SCREEN_MODE_PDF_VIEW_TOOL_BAR_SIZE, 200);
  }

  public void setPdfViewThumbnailsSizeInFullScreenMode(int pdfViewThumbnailsSizeInFullScreenMode) {
    this.config.setProperty(FULL_SCREEN_MODE_PDF_VIEW_TOOL_BAR_SIZE,
        pdfViewThumbnailsSizeInFullScreenMode);
  }

  public boolean isMaximizeStageAtStartup() {
    return this.config.getBoolean(STARTUP_MAXIMIZE_WINDOW, false);
  }

  public void setMaximizeStageAtStartup(boolean isMaximizeStageAtStartup) {
    this.config.setProperty(STARTUP_MAXIMIZE_WINDOW, isMaximizeStageAtStartup);
  }

  public boolean isAutoCompleteSuggestionsEnabledAtStartup() {
    return this.config.getBoolean(STARTUP_AUTOCOMPLETE_SUGGESTIONS_ENABLED, false);
  }

  public void setAutoCompleteSuggestionsEnabledAtStartup(
      boolean isAutoCompleteSuggestionsEnabledAtStartup) {
    this.config.setProperty(STARTUP_AUTOCOMPLETE_SUGGESTIONS_ENABLED,
        isAutoCompleteSuggestionsEnabledAtStartup);
  }

  public boolean isExpandAllTreeViewItems() {
    return this.config.getBoolean(EXPAND_ALL_TREE_VIEW_ITEMS, false);
  }

  public void setExpandAllTreeViewItems(boolean isExpandAllTreeViewItems) {
    this.config.setProperty(EXPAND_ALL_TREE_VIEW_ITEMS, isExpandAllTreeViewItems);
  }

  public boolean isDarkModeEnabled() {
    return this.config.getBoolean(DARK_MODE_ENABLED, true);
  }

  public void setDarkModeEnabled(boolean isDarkModeEnabled) {
    this.config.setProperty(DARK_MODE_ENABLED, isDarkModeEnabled);
  }
}
