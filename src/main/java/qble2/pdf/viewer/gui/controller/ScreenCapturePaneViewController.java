package qble2.pdf.viewer.gui.controller;

import java.net.URL;
import java.util.EventListener;
import java.util.Optional;
import java.util.ResourceBundle;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.robot.Robot;
import lombok.extern.slf4j.Slf4j;
import qble2.pdf.viewer.gui.PdfViewerConfig;
import qble2.pdf.viewer.gui.event.EventBusFx;

@Component
@Slf4j
public class ScreenCapturePaneViewController implements Initializable, EventListener {

  @Autowired
  private EventBusFx eventBusFx;

  @Autowired
  private PdfViewerConfig pdfViewerConfig;

  @FXML
  private BorderPane screenCapture;

  @FXML
  private Canvas canvas;

  //
  private GraphicsContext graphicsContext;
  private Point2D startingPoint;
  private Rectangle2D rectangle;

  private TriConsumer<String, String, Image> consumer;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    eventBusFx.registerListener(this);

    screenCapture.setVisible(false);

    canvas.widthProperty().bind(screenCapture.widthProperty());
    canvas.heightProperty().bind(screenCapture.heightProperty());

    graphicsContext = canvas.getGraphicsContext2D();
  }

  public void captureScreenRegion(TriConsumer<String, String, Image> callback) {
    this.consumer = callback;

    screenCapture.getScene().setCursor(Cursor.CROSSHAIR);
    screenCapture.setVisible(true);
  }

  /////
  /////
  /////

  @FXML
  private void mousePressed(MouseEvent event) {
    graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    startingPoint = getPoint(event);
  }

  @FXML
  private void mouseReleased(MouseEvent event) {
    screenCapture.getScene().setCursor(Cursor.DEFAULT);
    screenCapture.setVisible(false);

    Point2D currentPoint = getPoint(event);
    rectangle = createRectangle(startingPoint, currentPoint);

    openEditScreenCaptureDialog();
  }

  /////
  /////
  /////

  private Point2D getPoint(MouseEvent event) {
    // return new Point2D(event.getX(), event.getY());
    return new Point2D(event.getScreenX(), event.getScreenY());
  }

  private Rectangle2D createRectangle(Point2D p1, Point2D p2) {
    return new Rectangle2D(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()),
        Math.abs(p1.getX() - p2.getX()), Math.abs(p1.getY() - p2.getY()));
  }

  private void openEditScreenCaptureDialog() {
    if (rectangle.getWidth() <= 0 || rectangle.getHeight() <= 0) {
      log.warn("invalid screen region dimension (width: {} , height: {})", rectangle.getWidth(),
          rectangle.getHeight());
    } else {
      Robot robot = new Robot();
      WritableImage writableImage = robot.getScreenCapture(null, rectangle, false);
      ImageView screenCaptureImageView = new ImageView();
      screenCaptureImageView.setImage(writableImage);

      AddFileNoteDialogViewController addFileNoteDialog =
          new AddFileNoteDialogViewController(pdfViewerConfig.isDarkModeEnabled(), writableImage);
      Optional<AddFileNoteDialogResult> result = addFileNoteDialog.showAndWait();
      result.ifPresent(response -> consumer.accept(result.get().getFileNoteTitle(),
          result.get().getFileNoteComment(), result.get().getImage()));
    }

    // invalid dimension or on CANCEL
    consumer.accept(null, null, null);
  }

}
