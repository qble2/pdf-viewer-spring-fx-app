package qble2.pdf.viewer.gui.controller;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.EventListener;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import qble2.pdf.viewer.business.FileNote;
import qble2.pdf.viewer.gui.event.DeleteFileNoteEvent;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.ScrollToPdfFilePageEvent;
import qble2.pdf.viewer.gui.event.UpdateFileNoteEvent;

public class NoteDetailsViewController implements Initializable, EventListener {

  @FXML
  private TitledPane fileNoteDetailsTitledPane;

  @FXML
  private ImageView fileNoteImageView;

  @FXML
  private Label pageLabel;

  @FXML
  private TextArea commentTextArea;

  @FXML
  private Label lastUpdatedAtLabel;

  @FXML
  private Button updateFileNoteButton;

  //
  private FileNote fileNote;
  private EventBusFx eventBusFx;
  private BooleanProperty isPendingFileNoteUpdateBooleanProperty = new SimpleBooleanProperty(false);

  @Override
  public void initialize(URL location, ResourceBundle resources) {}

  public void init(FileNote fileNote, EventBusFx eventBusFx) {
    this.fileNote = fileNote;
    this.eventBusFx = eventBusFx;

    this.fileNoteDetailsTitledPane.setText(fileNote.getTitle());
    this.pageLabel.setText(String.valueOf(this.fileNote.getFilePage() + 1));
    this.commentTextArea.setText(this.fileNote.getComment());
    this.lastUpdatedAtLabel.setText(this.fileNote.getLastUpdatedAt()
        .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));

    fileNoteImageView.setImage(toImage(fileNote.getImageCapture().getWidth(),
        fileNote.getImageCapture().getHeight(), fileNote.getImageCapture().getBytes()));
    displayImagePortion();
    fileNoteImageView.setOnMouseClicked(e -> {
      // parents size is pref-based, everything will resize according to the new image size
      if (fileNoteImageView.getViewport() != null) {
        displayFullImage();
      } else {
        displayImagePortion();
      }
    });

    fileNoteDetailsTitledPane.expandedProperty().addListener((obs, oldValue, newValue) -> {
      if (!newValue) {
        displayImagePortion();
      }
    });

    updateFileNoteButton.disableProperty().bind(isPendingFileNoteUpdateBooleanProperty.not());
    commentTextArea.textProperty()
        .addListener((obs, oldValue, newValue) -> isPendingFileNoteUpdateBooleanProperty.set(true));
  }

  @FXML
  private void scrollToPage() {
    this.eventBusFx.notify(new ScrollToPdfFilePageEvent(fileNote.getFilePage()));
  }

  @FXML
  private void updateFileNote() {
    fileNote.setComment(commentTextArea.getText());
    this.eventBusFx.notify(new UpdateFileNoteEvent(fileNote, fileNoteDetailsTitledPane));
    isPendingFileNoteUpdateBooleanProperty.set(false);
  }

  @FXML
  private void deleteFileNote() {
    this.eventBusFx.notify(new DeleteFileNoteEvent(fileNote, fileNoteDetailsTitledPane));
  }

  /////
  /////
  /////

  private void displayFullImage() {
    fileNoteImageView.setViewport(null);
  }

  private void displayImagePortion() {
    fileNoteImageView.setViewport(new Rectangle2D(0, 0, 200d, 100d));
  }

  // PixelWriter generated image
  private Image toImage(int width, int height, byte[] pixelBytes) {
    WritableImage image = new WritableImage(width, height);
    image.getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(),
        pixelBytes, 0, width * 4);

    return image;
  }

}
