package qble2.pdf.viewer.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddFileNoteDialogViewController extends Dialog<AddFileNoteDialogResult>
    implements Initializable {

  @FXML
  private ImageView fileNoteImageView;

  @FXML
  private TextField noteTitleTextField;

  @FXML
  private TextArea noteCommentTextArea;

  public AddFileNoteDialogViewController(boolean isDarkMode, Image image) {
    try {
      FXMLLoader fxmlLoader =
          new FXMLLoader(getClass().getResource("/fxml/addFileNoteDialogPane.fxml"));
      fxmlLoader.setController(this);
      DialogPane dialogPane = fxmlLoader.load();

      setTitle("Add note");
      setDialogPane(dialogPane);

      //
      getDialogPane().getStylesheets().add(
          getClass().getResource(isDarkMode ? "/css/dark.css" : "/css/light.css").toExternalForm());
      fileNoteImageView.setImage(image);
      getDialogPane().lookupButton(ButtonType.OK).disableProperty()
          .bind(noteTitleTextField.textProperty().isEmpty());

      setResultConverter(buttonType -> {
        if (buttonType.getButtonData() == ButtonData.OK_DONE) {
          return new AddFileNoteDialogResult(noteTitleTextField.getText(),
              noteCommentTextArea.getText(), image);
        }
        return null;
      });
    } catch (IOException e) {
      log.error("an error has occured", e);
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {}

}


@Getter
@AllArgsConstructor
class AddFileNoteDialogResult {
  private String fileNoteTitle;
  private String fileNoteComment;
  private Image image;
}
