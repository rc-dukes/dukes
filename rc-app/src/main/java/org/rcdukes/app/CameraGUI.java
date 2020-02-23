package org.rcdukes.app;

import org.rcdukes.detect.CameraConfig;

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
  // make my config visible
  CameraConfig cameraConfig;
  // will be configured by main GUI and not via @FXML
  public LabeledValueSlider roiy;
  public LabeledValueSlider roih;

  @FXML
  public void initialize() {
    this.configureImageDisplaySize(400);
  }
  
  public CameraGUI() {
    super();
    cameraConfig=new CameraConfig();
  }

  private void configureImageDisplaySize(int fitWidth) {
    this.imageViewProperties(this.originalFrame, fitWidth);
    this.imageViewProperties(this.processedImage1, fitWidth);
    this.imageViewProperties(this.processedImage2, fitWidth);
  }

  public void applySliderValues() {
    cameraConfig.setRoih(roih.getValue());
    cameraConfig.setRoiy(roiy.getValue());
  }

}
