package nl.vaneijndhoven.dukes.app;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

/**
 * combined GUI for detectors
 *
 */
public class DukesFxGUI {
  @FXML private SplitPane splitPane;
  @FXML private TabPane tabPane;
  //Inject tab content.
  @FXML private Tab laneTab;
  @FXML private BorderPane laneDetection;
  @FXML private LaneDetectionGUI laneDetectionController;

  @FXML private Tab startTab;
  @FXML private BorderPane startDetection;
  @FXML private StartLightDetectionGUI startDetectionController;
  
  public void init() {
    tabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable,
                                                                    Tab oldValue, Tab newValue) -> {
        if (newValue == laneTab) {
            System.out.println("lane Mode");           
        } else if (newValue == startTab) {
            System.out.println("start Mode");
        }
    });
}
  
 }
