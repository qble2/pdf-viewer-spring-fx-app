package qble2.pdf.viewer.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.PinPdfFileEvent;

public class FilesTreeViewCellFactory implements Callback<TreeView<Path>, TreeCell<Path>> {

  private EventBusFx eventBusFx;

  public FilesTreeViewCellFactory(EventBusFx eventBusFx) {
    this.eventBusFx = eventBusFx;
  }

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
          // setTooltip(new Tooltip(isDirectory ? path.toString() : fileName));
          ImageView imageView = new ImageView();
          imageView.getStyleClass()
              .add(isDirectory ? "image-view-directory-icon" : "image-view-file-icon");
          setGraphic(imageView);

          // context menu
          MenuItem pinMenuItem = new MenuItem("pin to the right");
          pinMenuItem.setOnAction(e -> {
            eventBusFx.notify(new PinPdfFileEvent(path));
          });
          ContextMenu contextMenu = new ContextMenu(pinMenuItem);
          setContextMenu(contextMenu);
        }
      }
    };
  }

}
