package org.rcdukes.app;

import java.io.File;

import org.opencv.core.Mat;
import org.rcdukes.video.Image;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * combined GUI for detectors
 *
 */
public class DukesFxGUI extends BaseGUI implements GUIDisplayer {
  @FXML
  private VBox root;
  @FXML
  private VBox vbox;
  @FXML
  private MenuBar menuBar;
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
  private HBox camera;
  @FXML
  private CameraGUI cameraController;

  @FXML
  private Tab startTab;
  @FXML
  private VBox startDetection;
  @FXML
  private StartLightDetectionGUI startDetectionController;
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
  protected Button fullScreenButton;
  @FXML
  protected Button hideMenuButton;
  @FXML
  protected Button cameraButton;
  @FXML
  protected ComboBox<String> lanevideo;
  @FXML
  protected ComboBox<String> startvideo;
  @FXML
  protected TextArea messageArea;

  @FXML
  protected LabeledValueSlider roiy;

  @FXML
  protected LabeledValueSlider roih;

  // FXML label to show the current values set with the sliders
  @FXML
  private Label currentValues;
  // property for object binding
  private ObjectProperty<String> currentValuesProp;

  enum DisplayMode {
    Lane, Start
  }

  DisplayMode displayMode = DisplayMode.Lane;
 

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
    this.displayer = this;
    this.laneDetectionController.setDisplayer(this);
    this.startDetectionController.setDisplayer(this);
    // bind a text property with the string containing the current Values of
    currentValuesProp = new SimpleObjectProperty<>();
    this.currentValues.textProperty().bind(currentValuesProp);
    this.lanevideo.getItems().setAll(
        "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f2/4_lane_highway_roads_in_India_NH_48_Karnataka_3.jpg/1280px-4_lane_highway_roads_in_India_NH_48_Karnataka_3.jpg",
        "http://picaro/html/cam_pic_new.php",
        "http://picarford:8080/?action=stream");
    this.cameraController.roiy = roiy;
    this.cameraController.roih = roih;
  }

  @FXML
  public void initialize() {
    this.setMenuButtonIcon(homeButton, MaterialDesignIcon.HOME);
    this.setMenuButtonIcon(detectButton, MaterialDesignIcon.CAMERA);
    this.setMenuButtonIcon(githubButton, MaterialDesignIcon.GITHUB_BOX);
    this.setMenuButtonIcon(chatButton, MaterialDesignIcon.COMMENT_TEXT);
    this.setMenuButtonIcon(helpButton, FontAwesomeIcon.QUESTION_CIRCLE);
    this.setMenuButtonIcon(fullScreenButton, MaterialDesignIcon.FULLSCREEN);
    this.setMenuButtonIcon(hideMenuButton, MaterialDesignIcon.MENU_DOWN);
    this.lanevideo.setValue("http://wiki.bitplan.com/videos/full_run.mp4");
    this.startvideo.setValue("http://wiki.bitplan.com/videos/startlamp2.m4v");
  }

  @FXML
  public void startCamera() throws Exception {
    switch (displayMode) {
    case Lane:
      this.laneDetectionController.startCamera(this.cameraController);
      break;
    case Start:
      this.startDetectionController.startCamera();
      break;
    default:
      break;
    }
  }

  @Override
  public void displayOriginal(Image image) {
    displayImage(cameraController.originalFrame, image);
  }

  @Override
  public void displayOriginal(Mat openCvImage) {
    this.displayImage(cameraController.originalFrame, openCvImage);
  }

  @Override
  public void display1(Image image) {
    displayImage(cameraController.processedImage1, image);
  }

  @Override
  public void display2(Image image) {
    displayImage(cameraController.processedImage2, image);
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
      this.lanevideo.setValue(file.getPath());
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
   * fullscreen toggle clicked
   * 
   * @param event
   */
  @FXML
  private void onFullScreen(final ActionEvent event) {
    this.primaryStage.setMaximized(!primaryStage.isMaximized());
    // this.primaryStage.setFullScreen(!primaryStage.isFullScreen());
    addJustFullScreenButton();
  }

  public void addJustFullScreenButton() {
    if (primaryStage.isMaximized()) {
      this.setMenuButtonIcon(fullScreenButton, MaterialDesignIcon.FULLSCREEN_EXIT);
      setButtonTooltip(fullScreenButton, "part Screen");
    } else {
      this.setMenuButtonIcon(fullScreenButton, MaterialDesignIcon.FULLSCREEN);
      setButtonTooltip(fullScreenButton, "full Screen");
    }

  }

  @FXML
  private void onHideMenu(final ActionEvent event) {
    showMenuBar(primaryStage.getScene(), menuBar, !menuBar.isVisible());
  }

  /**
   * show or hide the menuBar
   * 
   * @param scene
   * @param pMenuBar
   */
  public void showMenuBar(Scene scene, MenuBar pMenuBar, boolean show) {
    Parent sroot = scene.getRoot();
    ObservableList<Node> rootChilds = null;
    if (sroot instanceof VBox)
      rootChilds = ((VBox) sroot).getChildren();
    if (rootChilds == null)
      throw new RuntimeException(
          "showMenuBar can not handle scene root of type "
              + sroot.getClass().getName());
    if (!show && rootChilds.contains(pMenuBar)) {
      rootChilds.remove(pMenuBar);
    } else if (show) {
      rootChilds.add(0, pMenuBar);
    }
    pMenuBar.setVisible(show);
    if (pMenuBar.isVisible()) {
      this.setMenuButtonIcon(hideMenuButton, MaterialDesignIcon.MENU_DOWN);
      setButtonTooltip(hideMenuButton, "hide Menu");
    } else {
      this.setMenuButtonIcon(hideMenuButton, MaterialDesignIcon.MENU_UP);
      setButtonTooltip(hideMenuButton, "show Menu");
    }
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
  public Property<String> getLaneVideoProperty() {
    return this.lanevideo.valueProperty();
  }

  @Override
  public Property<String> getStartVideoProperty() {
    return this.startvideo.valueProperty();
  }

  @Override
  public void setMessageText(String text) {
    this.messageArea.setText(text);
  }

}
