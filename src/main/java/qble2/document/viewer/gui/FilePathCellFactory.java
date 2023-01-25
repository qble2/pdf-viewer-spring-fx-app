package qble2.document.viewer.gui;

import java.nio.file.Path;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

public class FilePathCellFactory implements Callback<ListView<Path>, ListCell<Path>> {

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
        }
      };
    };
  }

}
