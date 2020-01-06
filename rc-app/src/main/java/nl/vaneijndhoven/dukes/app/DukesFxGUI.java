package nl.vaneijndhoven.dukes.app;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.opencv.core.Mat;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import nl.vaneijndhoven.dukes.common.ErrorHandler;

/**
 * combined GUI for detectors
 *
 */
public class DukesFxGUI extends BaseGUI implements GUIDisplayer {
  @FXML
  private GridPane root;
  @FXML
  private VBox vbox;
  @FXML
  private MenuBar menuBar;

  @FXML
  private SplitPane splitPane;
  @FXML
  private HBox videoBox;
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
  private VBox navigation;
  @FXML
  private NavigationGUI navigationController;

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
  protected Button homeButton;
  @FXML
  protected Button detectButton;
  @FXML
  protected Button githubButton;
  @FXML
  protected Button chatButton;
  @FXML
  protected Button helpButton;
  @FXML
  protected Button cameraButton;
  @FXML
  protected TextField lanevideo;
  @FXML
  protected TextField startvideo;
  @FXML
  protected TextArea messageArea;

  // FXML label to show the current values set with the sliders
  @FXML
  private Label currentValues;
  // property for object binding
  private ObjectProperty<String> currentValuesProp;

  enum DisplayMode {
    Lane, Start
  }

  DisplayMode displayMode = DisplayMode.Lane;
  private Stage primaryStage;

  /**
   * initialize me
   * 
   * @param primaryStage
   */
  public void init(Stage primaryStage) {
    this.primaryStage = primaryStage;
    tabPane.getSelectionModel().selectedItemProperty()
        .addListener((ObservableValue<? extends Tab> observable, Tab oldValue,
            Tab newValue) -> {
          if (newValue == laneTab) {
            displayMode = DisplayMode.Lane;
          } else if (newValue == startTab) {
            displayMode = DisplayMode.Start;
          }
        });
    this.configureImageDisplaySize(400);
    this.displayer = this;
    this.laneDetectionController.setDisplayer(this);
    this.startDetectionController.setDisplayer(this);
    // bind a text property with the string containing the current Values of
    currentValuesProp = new SimpleObjectProperty<>();
    this.currentValues.textProperty().bind(currentValuesProp);

  }

  @FXML
  public void initialize() {
    this.setButtonIcon(homeButton, MaterialDesignIcon.HOME);
    this.setButtonIcon(detectButton, MaterialDesignIcon.CAMERA);
    this.setButtonIcon(githubButton, MaterialDesignIcon.GITHUB_BOX);
    this.setButtonIcon(chatButton, MaterialDesignIcon.COMMENT_TEXT);
    this.setButtonIcon(helpButton, FontAwesomeIcon.QUESTION_CIRCLE);
    this.lanevideo.setText("http://wiki.bitplan.com/videos/full_run.mp4");
    this.startvideo.setText("http://wiki.bitplan.com/videos/startlamp2.m4v");
  }

  private void configureImageDisplaySize(int fitWidth) {
    this.imageViewProperties(this.originalFrame, fitWidth);
    this.imageViewProperties(this.processedImage1, fitWidth);
    this.imageViewProperties(this.processedImage2, fitWidth);
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
    displayImage(originalFrame, imageFrame);
  }

  @Override
  public void displayOriginal(Mat openCvImage) {
    this.displayImage(originalFrame, openCvImage);
  }

  @Override
  public void display1(byte[] imageFrame) {
    displayImage(this.processedImage1, imageFrame);
  }

  @Override
  public void display2(byte[] imageFrame) {
    displayImage(this.processedImage2, imageFrame);
  }

  @Override
  public void setCameraButtonText(String text) {
    this.cameraButton.setText(text);
  }

  @Override
  public void showCurrentValues(String text) {
    System.out.println(text);
    this.onFXThread(this.currentValuesProp, text);
  }

  /**
   * File/Open clicked
   * 
   * @param event
   *          Event on "File/Open" menu item.
   */
  @FXML
  private void onOpen(final ActionEvent event) {
    final FileChooser fileChooser = new FileChooser();
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
        "Video and Image Files", "*.jpg", "*.png", "*.avi", "*.mp4", "*.m4v");
    fileChooser.getExtensionFilters().add(extFilter);
    File file = fileChooser.showOpenDialog(primaryStage);
    if (file != null) {
      this.lanevideo.setText(file.getPath());
    }
  }

  /**
   * File/Quit clicked
   * 
   * @param event
   *          Event on "File/Quit" menu item.
   */
  @FXML
  private void onQuit(final ActionEvent event) {
    Platform.exit();
    System.exit(0);
  }

  /**
   * Online Manual clicked
   * 
   * @param event
   *          Event on "Help/Online Manual" menu item.
   */
  @FXML
  private void onHelp(final ActionEvent event) {
    this.help();
  }

  /**
   * Report Issue clicked
   * 
   * @param event
   *          Event on "Help/" menu item.
   */
  @FXML
  private void onReportIssue(final ActionEvent event) {
    this.reportIssue();
  }

  /**
   * Chat clicked
   * 
   * @param event
   *          Event e.g. Button chat
   */
  @FXML
  private void onChat(final ActionEvent event) {
    this.linkToChat();
  }

  /**
   * Help/About clicked
   * 
   * @param event
   *          Event on "Help/About" menu item.
   */
  @FXML
  private void onHelpAbout(final ActionEvent event) {
    this.helpAbout();
  }

  /**
   * Handle action related to input (in this case specifically only responds to
   * keyboard event CTRL-A).
   * 
   * @param event
   *          Input event.
   */
  @FXML
  private void handleKeyInput(final InputEvent event) {
    if (event instanceof KeyEvent) {
      final KeyEvent keyEvent = (KeyEvent) event;
      if (isKey(keyEvent, KeyCode.A))
        helpAbout();
      if (isKey(keyEvent, KeyCode.H))
        help();
    }
  }

  public boolean isKey(KeyEvent keyEvent, KeyCode keyCode) {
    return keyEvent.isControlDown() && keyEvent.getCode() == keyCode;
  }

  private void help() {
    browse("http://wiki.bitplan.com/index.php/Self_Driving_RC_Car/App");
  }

  private void reportIssue() {
    browse("https://github.com/rc-dukes/dukes/issues/new");
  }

  /**
   * "About" menu selected or CTRL-A pressed.
   */
  private void helpAbout() {
    browse("https://github.com/rc-dukes/dukes");
  }

  private void linkToChat() {
    browse("https://gitter.im/rc-dukes/community");
  }

  @Override
  public StringProperty getLaneVideoProperty() {
    return this.lanevideo.textProperty();
  }

  @Override
  public StringProperty getStartVideoProperty() {
    return this.startvideo.textProperty();
  }

  @Override
  public void setMessageText(String text) {
    this.messageArea.setText(text);
  }
}
