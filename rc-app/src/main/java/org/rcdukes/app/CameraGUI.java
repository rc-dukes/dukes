package org.rcdukes.app;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

/**
 * camera GUI
 * 
 * @author wf
 */
public class CameraGUI extends BaseGUI {
  // the FXML area for showing the current frame
  @FXML ImageView originalFrame;
  @FXML ImageView processedImage1;
  @FXML ImageView processedImage2;

  @FXML
  public void initialize() {
    this.configureImageDisplaySize(400);
  }

  private void configureImageDisplaySize(int fitWidth) {
    this.imageViewProperties(this.originalFrame, fitWidth);
    this.imageViewProperties(this.processedImage1, fitWidth);
    this.imageViewProperties(this.processedImage2, fitWidth);
  }

}
