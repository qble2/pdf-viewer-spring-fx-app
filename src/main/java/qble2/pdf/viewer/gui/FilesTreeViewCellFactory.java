package qble2.pdf.viewer.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class FilesTreeViewCellFactory implements Callback<TreeView<Path>, TreeCell<Path>> {

  private final Image folderIconImage =
      new Image(getClass().getResourceAsStream("/image/material/outline_folder_black_24dp.png"));
  private final Image fileIconImage = new Image(
      getClass().getResourceAsStream("/image/material/outline_description_black_24dp.png"));

  @Override
  public TreeCell<Path> call(TreeView<Path> param) {
    return new TreeCell<>() {
      protected void updateItem(Path path, boolean empty) {
        super.updateItem(path, empty);

        if (empty || path == null) {
          setText(null);
          setGraphic(null);
          setStyle(null);
        } else {
          String fileName = path.getFileName().toString();
          setText(fileName);

          boolean isDirectory = Files.isDirectory(path);
          setTooltip(new Tooltip(isDirectory ? path.toString() : fileName));
          setGraphic(new ImageView(isDirectory ? folderIconImage : fileIconImage));
        }
      }
    };
  }

}
