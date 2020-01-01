package nl.vaneijndhoven.dukes.app;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class BaseGUI {
  @FXML protected Button cameraButton;
  // the FXML area for showing the current frame
  @FXML protected ImageView originalFrame;
  
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
