package nl.vaneijndhoven.dukes.app;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

/**
 * combined GUI for detectors
 *
 */
public class DukesFxGUI {
  @FXML private TabPane tabPane;
  //Inject tab content.
  @FXML private Tab laneTab;
  @FXML private BorderPane laneDetection;
  @FXML private LaneDetectionGUI laneDetectionController;

  @FXML private Tab startTab;
  @FXML private BorderPane startDetection;
  @FXML private StartLightDetectionGUI startDetectionController;
  
 }
