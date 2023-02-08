package qble2.pdf.viewer.gui;

import java.nio.file.Path;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.PinPdfFileEvent;

public class FileListViewCellFactory implements Callback<ListView<Path>, ListCell<Path>> {

  private EventBusFx eventBusFx;

  public FileListViewCellFactory(EventBusFx eventBusFx) {
    this.eventBusFx = eventBusFx;
  }

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
          // setTooltip(new Tooltip(fileName));

          ImageView imageView = new ImageView();
          imageView.getStyleClass().add("image-view-file-icon");
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
