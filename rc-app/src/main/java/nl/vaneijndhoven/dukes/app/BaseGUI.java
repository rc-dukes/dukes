package nl.vaneijndhoven.dukes.app;

import java.io.ByteArrayInputStream;

import org.opencv.core.Mat;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nl.vaneijndhoven.opencv.video.ImageUtils;

public class BaseGUI {
  @FXML protected Button cameraButton;
  // the FXML area for showing the current frame
  @FXML protected ImageView originalFrame;
  
  /**
   * show the given imageFrame in the given JavaFX imageView Control
   * @param imageView
   * @param imageFrame
   */
  protected void displayImage(ImageView imageView, byte[] imageFrame) {
    if (imageFrame==null) return;
    Image image= new Image(new ByteArrayInputStream(imageFrame));
    this.onFXThread(imageView.imageProperty(),image);
  }
  
  protected void displayImage(ImageView fxImage, Mat openCvImage) {
    if (openCvImage.rows()>0) {
      Image image=ImageUtils.mat2Image(openCvImage);
      this.onFXThread(fxImage.imageProperty(), image);
    }
  }
  
  /**
   * Set typical {@link ImageView} properties: a fixed width and the information
   * to preserve the original image ration
   *
   * @param image
   *          the {@link ImageView} to use
   * @param dimension
   *          the width of the image to set
   */
  protected void imageViewProperties(ImageView image, int dimension) {
    // set a fixed width for the given ImageView
    image.setFitWidth(dimension);
    // preserve the image ratio
    image.setPreserveRatio(true);
  }

  
  /**
   * Generic method for putting element running on a non-JavaFX thread on the
   * JavaFX thread, to properly update the UI
   *
   * @param property
   *          a {@link ObjectProperty}
   * @param value
   *          the value to set for the given {@link ObjectProperty}
   */
  protected <T> void onFXThread(final ObjectProperty<T> property, final T value) {
    Platform.runLater(() -> property.set(value));
  }
}
