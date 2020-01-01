package nl.vaneijndhoven.dukes.app;

import org.opencv.core.Mat;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * combined GUI for detectors
 *
 */
public class DukesFxGUI extends BaseGUI implements GUIDisplayer {
  @FXML
  private SplitPane splitPane;
  @FXML
  private TabPane tabPane;
  // Inject tab content.
  @FXML
  private Tab laneTab;
  @FXML
  private VBox laneDetection;
  @FXML
  private LaneDetectionGUI laneDetectionController;

  @FXML
  private Tab startTab;
  @FXML
  private VBox startDetection;
  @FXML
  private StartLightDetectionGUI startDetectionController;
  // the FXML area for showing the current frame
  @FXML
  protected ImageView originalFrame;
  @FXML
  private ImageView processedImage1;
  @FXML
  private ImageView processedImage2;
  @FXML
  protected Button cameraButton;
  // FXML label to show the current values set with the sliders
  @FXML
  private Label currentValues;
  // property for object binding
  private ObjectProperty<String> currentValuesProp;

  enum DisplayMode {
    Lane, Start
  }

  DisplayMode displayMode = DisplayMode.Lane;

  public void init() {
    tabPane.getSelectionModel().selectedItemProperty()
        .addListener((ObservableValue<? extends Tab> observable, Tab oldValue,
            Tab newValue) -> {
          if (newValue == laneTab) {
            displayMode = DisplayMode.Lane;
          } else if (newValue == startTab) {
            displayMode = DisplayMode.Start;
          }
        });
      this.configureImageDisplaySize();
      this.displayer=this;
      this.laneDetectionController.setDisplayer(this);
      this.startDetectionController.setDisplayer(this);
      // bind a text property with the string containing the current Values of
      currentValuesProp = new SimpleObjectProperty<>();
      this.currentValues.textProperty().bind(currentValuesProp);
  }

  private void configureImageDisplaySize() {
    this.imageViewProperties(this.originalFrame, 400);
    this.imageViewProperties(this.processedImage1, 400);
    this.imageViewProperties(this.processedImage2, 400);
  }

  @FXML
  public void startCamera() throws Exception {
    switch (displayMode) {
    case Lane:
      this.laneDetectionController.startCamera();
      break;
    case Start:
      this.startDetectionController.startCamera();
      break;
    default:
      break;
    }
  }

  @Override
  public void displayOriginal(byte[] imageFrame) {   
    displayImage(originalFrame,imageFrame);
  }
  
  @Override
  public void displayOriginal(Mat openCvImage) {
    this.displayImage(originalFrame, openCvImage);
  }

  @Override
  public void display1(byte[] imageFrame) {
    displayImage(this.processedImage1,imageFrame);
  }

  @Override
  public void display2(byte[] imageFrame) {
    displayImage(this.processedImage2,imageFrame);
  }

  @Override
  public void setCameraButtonText(String text) {
    this.cameraButton.setText("Start Camera");
  }

  @Override
  public void showCurrentValues(String text) {
    System.out.println(text);
    this.onFXThread(this.currentValuesProp, text);
  }
}
