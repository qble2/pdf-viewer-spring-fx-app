package qble2.pdf.viewer.gui;

import java.nio.file.Path;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class FileListViewCellFactory implements Callback<ListView<Path>, ListCell<Path>> {

  private final Image fileIconImage = new Image(
      getClass().getResourceAsStream("/image/material/outline_description_black_24dp.png"));

  @Override
  public ListCell<Path> call(ListView<Path> param) {
    return new ListCell<>() {
      protected void updateItem(Path path, boolean empty) {
        super.updateItem(path, empty);

        if (empty || path == null) {
          setText(null);
          setGraphic(null);
          setStyle(null);
        } else {
          String fileName = path.getFileName().toString();
          setText(fileName);
          setTooltip(new Tooltip(fileName));

          setGraphic(new ImageView(fileIconImage));
        }
      }
    };
  }

}
