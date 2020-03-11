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
  @FXML ImageView processedImage3;
  
  // make my config visible
  CameraConfig cameraConfig;
  // will be configured by main GUI and not via @FXML
  public LabeledValueSlider roiy;
  public LabeledValueSlider roih;
  public LabeledValueSlider angleOffset;
  public int imageWidth=800;
  @FXML
  public void initialize() {
    int fitWidth = (int)(BaseGUI.getScreenWidth()/1920.0*imageWidth);
    this.configureImageDisplaySize(fitWidth);
  }
  
  public CameraGUI() {
    super();
    cameraConfig=new CameraConfig();
  }

  /**
   * configure the image display size to the given fitWidth
   * @param fitWidth
   */
  private void configureImageDisplaySize(int fitWidth) {
    this.imageViewProperties(this.originalFrame, fitWidth);
    this.imageViewProperties(this.processedImage1, fitWidth*3/4);
    this.imageViewProperties(this.processedImage2, fitWidth*3/4);
  }

  public void applySliderValues() {
    cameraConfig.setAngleOffset(angleOffset.getValue());
    cameraConfig.setRoih(roih.getValue());
    cameraConfig.setRoiy(roiy.getValue());
  }

}
