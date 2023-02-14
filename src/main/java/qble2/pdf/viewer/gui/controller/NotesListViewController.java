package qble2.pdf.viewer.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.List;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.layout.Region;
import lombok.extern.slf4j.Slf4j;
import qble2.pdf.viewer.business.FileNote;
import qble2.pdf.viewer.business.FileNoteService;
import qble2.pdf.viewer.business.ImageCapture;
import qble2.pdf.viewer.gui.event.DeleteFileNoteEvent;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.FileSelectionChangedEvent;
import qble2.pdf.viewer.gui.event.FullScreenModeEvent;
import qble2.pdf.viewer.gui.event.PageSelectionChangedEvent;
import qble2.pdf.viewer.gui.event.UpdateFileNoteEvent;

@Component
@Slf4j
public class NotesListViewController implements Initializable, EventListener {

  @Value("classpath:/fxml/fileNoteDetails.fxml")
  private Resource noteDetailsFxmlResource;

  @Autowired
  private EventBusFx eventBusFx;

  @Autowired
  private FileNoteService fileNoteService;

  @Autowired
  private ScreenCapturePaneViewController screenCapturePaneController;

  @FXML
  private TitledPane fileNotesViewTitledPane;

  @FXML
  private Accordion fileNotesAccordion;

  //
  private ObjectProperty<Path> selectedPdfFilePathObjectProperty = new SimpleObjectProperty<>();
  private IntegerProperty selectedPdfFilePageIntegerProperty = new SimpleIntegerProperty(0);
  private BooleanProperty isScreenCaptureInProgressBooleanProperty =
      new SimpleBooleanProperty(false);
  private BooleanProperty isFullScreenModeBooleanProperty = new SimpleBooleanProperty(false);

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);

    fileNotesViewTitledPane.setMaxWidth(Region.USE_PREF_SIZE);
    fileNotesViewTitledPane.setMaxHeight(Region.USE_PREF_SIZE);

    fileNotesViewTitledPane.managedProperty().bind(fileNotesViewTitledPane.visibleProperty());
    fileNotesViewTitledPane.visibleProperty().bind(
        selectedPdfFilePathObjectProperty.isNotNull().and(isScreenCaptureInProgressBooleanProperty
            .not().and(isFullScreenModeBooleanProperty.not())));

    // auto-resize when children resize (ImageView)
    fileNotesViewTitledPane.setMaxWidth(Region.USE_PREF_SIZE);
    fileNotesViewTitledPane.setMaxHeight(Region.USE_PREF_SIZE);
  }

  @FXML
  private void addFileNote() {
    isScreenCaptureInProgressBooleanProperty.set(true);

    screenCapturePaneController
        .captureScreenRegion((fileNoteTitle, fileNoteComment, fileNoteImage) -> {
          if (fileNoteImage != null) {
            FileNote fileNote =
                FileNote.builder().filePath(selectedPdfFilePathObjectProperty.get().toString())
                    .filePage(selectedPdfFilePageIntegerProperty.get()).title(fileNoteTitle)
                    .comment(fileNoteComment)
                    .imageCapture(new ImageCapture((int) fileNoteImage.getWidth(),
                        (int) fileNoteImage.getHeight(), toByteArray(fileNoteImage)))
                    .build();
            fileNoteService.createFileNote(fileNote);
            loadFileNotes(true);
          }

          isScreenCaptureInProgressBooleanProperty.set(false);
        });
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processFileSelectionChangedEvent(FileSelectionChangedEvent event) {
    selectedPdfFilePathObjectProperty.set(event.getFilePath());
    if (event.getFilePath() != null) {
      loadFileNotes(false);
    }
  }

  @Subscribe
  public void processPageSelectionChangedEvent(PageSelectionChangedEvent event) {
    selectedPdfFilePageIntegerProperty.set(event.getPage());
  }

  @Subscribe
  private void processUpdateFileNoteEvent(UpdateFileNoteEvent event) {
    this.fileNoteService.updateFileNote(event.getFileNote());
    loadFileNotes(true);
  }

  @Subscribe
  private void processDeleteFileNoteEvent(DeleteFileNoteEvent event) {
    this.fileNoteService.deleteFileNote(event.getFileNote().getId());
    fileNotesAccordion.getPanes().remove(event.getNode());
  }

  @Subscribe
  public void processFullScreenModeEvent(FullScreenModeEvent event) {
    this.isFullScreenModeBooleanProperty.set(event.isFullScreen());
  }

  /////
  /////
  /////

  private void loadFileNotes(boolean isExpandFirstChild) {
    List<FileNote> fileNotes =
        this.fileNoteService.getFileNotes(this.selectedPdfFilePathObjectProperty.get().toString());
    fileNotesAccordion.getPanes().clear();
    fileNotesAccordion.getPanes()
        .addAll(fileNotes.stream().map(this::createNoteDetailsView).toList());
    if (fileNotesAccordion.getPanes().size() > 0 && isExpandFirstChild) {
      fileNotesAccordion.getPanes().get(0).setExpanded(true);
    }
  }

  private TitledPane createNoteDetailsView(FileNote fileNote) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(noteDetailsFxmlResource.getURL());
      TitledPane parent = fxmlLoader.load();
      NoteDetailsViewController noteController =
          fxmlLoader.<NoteDetailsViewController>getController();
      noteController.init(fileNote, eventBusFx);
      return parent;
    } catch (IOException e) {
      log.error("an error has occured", e);
    }

    return new TitledPane("!Failed to load file note!", null);
  }

  // PixelReader generated image bytes
  private byte[] toByteArray(Image image) {
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    byte[] pixelBytes = new byte[width * height * 4];

    image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(),
        pixelBytes, 0, width * 4);

    return pixelBytes;
  }

}
