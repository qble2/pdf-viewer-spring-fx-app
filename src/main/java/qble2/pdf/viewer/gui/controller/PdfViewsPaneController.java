package qble2.pdf.viewer.gui.controller;

import java.net.URL;
import java.util.EventListener;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import qble2.pdf.viewer.gui.event.EventBusFx;
import qble2.pdf.viewer.gui.event.SecondViewOpenedTabsCountChangedEvent;

@Component
public class PdfViewsPaneController implements Initializable, EventListener {

  @Autowired
  private EventBusFx eventBusFx;

  @FXML
  private SplitPane splitPane;

  @FXML
  private Parent pdfView;

  @FXML
  private Parent secondViewTabPane;

  //
  private IntegerProperty secondViewOpenedTabsCountIntegerProperty = new SimpleIntegerProperty(0);

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);

    // this prevents the divider to be mispositioned on parent/scene size change
    SplitPane.setResizableWithParent(pdfView, Boolean.FALSE);
    SplitPane.setResizableWithParent(secondViewTabPane, Boolean.FALSE);

    showDivider(false);
  }

  /////
  ///// events
  /////

  @Subscribe
  public void processSecondViewOpenedTabsCountChangedEvent(
      SecondViewOpenedTabsCountChangedEvent event) {
    if (event.getOpenedTabsCount() == 0) {
      showDivider(false);
    } else if (secondViewOpenedTabsCountIntegerProperty.get() == 0) {
      // first tab opened
      showDivider(true);
    }

    secondViewOpenedTabsCountIntegerProperty.set(event.getOpenedTabsCount());
  }

  /////
  /////
  /////

  // show/hide somewhat the divider
  private void showDivider(boolean isDividerVisible) {
    splitPane.setDividerPosition(0, isDividerVisible ? 0.5d : 1d);
  }

}
