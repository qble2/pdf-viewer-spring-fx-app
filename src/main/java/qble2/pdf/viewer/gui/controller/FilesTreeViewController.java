package qble2.pdf.viewer.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.ResourceBundle;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.eventbus.Subscribe;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import qble2.pdf.viewer.gui.FilesTreeViewCellFactory;
import qble2.pdf.viewer.gui.FilterableTreeItem;
import qble2.pdf.viewer.gui.PdfViewerConfig;
import qble2.pdf.viewer.gui.event.AutoCompleteSelectionChangedEvent;
import qble2.pdf.viewer.gui.event.ClearFileSelectionEvent;
import qble2.pdf.viewer.gui.event.DirectoryChangedEvent;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.FileSelectionChangedEvent;

@Component
@Slf4j
public class FilesTreeViewController implements Initializable, EventListener {

  @Autowired
  private EventBusFx eventBusFx;

  @Autowired
  private PdfViewerConfig pdfViewerConfig;

  @FXML
  private Parent root;

  @FXML
  private ScrollPane filesTreeViewScrollPane;

  @FXML
  private TreeView<Path> filesTreeView;

  //
  private ObjectProperty<Path> currentDirectoryPathObjectProperty = new SimpleObjectProperty<>();
  private StringProperty filterStringProperty = new SimpleStringProperty();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);

    root.managedProperty().bind(root.visibleProperty());

    filesTreeView.setCellFactory(new FilesTreeViewCellFactory(eventBusFx));
    filesTreeView.getSelectionModel().selectedItemProperty()
        .addListener((obs, oldValue, newValue) -> {
          if (newValue != null && Files.isRegularFile(newValue.getValue())) {
            eventBusFx.notify(new FileSelectionChangedEvent(newValue.getValue()));
          } else {
            eventBusFx.notify(new FileSelectionChangedEvent(null));
          }
        });
  }

  @FXML
  private void expandCollapseTreeItemOnSingleClick(MouseEvent event) {
    TreeItem<Path> treeItem = filesTreeView.getSelectionModel().getSelectedItem();
    if (treeItem != null && event.getButton() == MouseButton.PRIMARY) {
      treeItem.setExpanded(!treeItem.isExpanded());
    }
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processDirectoryChangedEvent(DirectoryChangedEvent event) {
    this.currentDirectoryPathObjectProperty.set(event.getDirectoryPath());
    if (currentDirectoryPathObjectProperty.get() != null) {
      log.info("Reloading tree view for directory: {}", currentDirectoryPathObjectProperty.get());

      FilterableTreeItem<Path> rootItem =
          new FilterableTreeItem<>(currentDirectoryPathObjectProperty.get());
      rootItem.setExpanded(true);
      this.filesTreeView.setRoot(rootItem);

      populateTreeView(currentDirectoryPathObjectProperty.get(), rootItem);
      // filesTreeView.setShowRoot(false);

      // treeViewSearchTextField.textProperty()
      rootItem.predicateProperty().bind(Bindings.createObjectBinding(() -> {
        if (StringUtils.isBlank(filterStringProperty.get())) {
          return null;
        }

        return path -> path.getFileName().toString().toLowerCase()
            .contains(filterStringProperty.get().toLowerCase());
      }, filterStringProperty));
    }
  }

  @Subscribe
  public void processClearFileSelectionEvent(ClearFileSelectionEvent event) {
    filterStringProperty.set(null);
    this.filesTreeView.getSelectionModel().clearSelection();
  }

  @Subscribe
  public void processAutoCompleteSelectionChangedEvent(AutoCompleteSelectionChangedEvent event) {
    filterStringProperty.set(event.getSelection());
  }

  /////
  /////
  /////

  private void populateTreeView(Path path, FilterableTreeItem<Path> parent) {
    boolean isExpandAllTreeViewItems = pdfViewerConfig.isExpandAllTreeViewItems();

    try {
      if (Files.isDirectory(path)) {
        FilterableTreeItem<Path> treeItem;
        if (path.equals(parent.getValue())) {
          treeItem = parent;
        } else {
          treeItem = new FilterableTreeItem<>(path);
          treeItem.setExpanded(isExpandAllTreeViewItems);
          parent.getSourceChildren().add(treeItem);
        }

        Files.list(path).forEach(p -> {
          populateTreeView(p, treeItem);
        });
      } else if ("pdf".equals(FilenameUtils.getExtension(path.getFileName().toString()))) {
        parent.getSourceChildren().add(new FilterableTreeItem<>(path));
      }
    } catch (IOException e) {
      log.error("An error has occurred", e);
    }
  }

  // XXX duplicated root
  // private void populateTreeView(Path path, FilterableTreeItem<Path> parent) {
  // try {
  // if (Files.isDirectory(path)) {
  // FilterableTreeItem<Path> treeItem = new FilterableTreeItem<>(path);
  // parent.getSourceChildren().add(treeItem);
  //
  // Files.list(path).forEach(p -> {
  // populateTreeView(p, treeItem);
  // });
  // } else if ("pdf".equals(FilenameUtils.getExtension(path.getFileName().toString()))) {
  // parent.getSourceChildren().add(new TreeItem<>(path));
  // }
  // } catch (IOException e) {
  // log.error("An error has occurred", e);
  // }
  // }

}
