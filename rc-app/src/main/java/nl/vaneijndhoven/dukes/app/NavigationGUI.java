package nl.vaneijndhoven.dukes.app;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * navigation GUI
 * @author wf
 */
public class NavigationGUI extends BaseGUI {
  @FXML
  protected Button upButton;
  @FXML
  protected Button autoPilotButton;
  @FXML
  protected Button leftButton;
  @FXML
  protected Button rightButton;
  @FXML
  protected Button stopButton;
  @FXML
  protected Button downButton;
  @FXML
  protected Button manualButton;
  @FXML
  protected Button brakeButton;
  @FXML
  protected Button centerButton;
  @FXML
  protected Button powerButton;
  
  @FXML
  public void initialize() {
    setButtonIcon(upButton,MaterialDesignIcon.ARROW_UP_BOLD);
    setButtonIcon(autoPilotButton,MaterialDesignIcon.CAR);
    setButtonIcon(leftButton,MaterialDesignIcon.ARROW_LEFT_BOLD);
    setButtonIcon(stopButton,MaterialDesignIcon.STOP);
    setButtonIcon(rightButton,MaterialDesignIcon.ARROW_RIGHT_BOLD);
    setButtonIcon(downButton,MaterialDesignIcon.ARROW_DOWN_BOLD);
    setButtonIcon(manualButton,MaterialDesignIcon.ACCOUNT);
    setButtonIcon(brakeButton, MaterialDesignIcon.CLOSE);
    setButtonIcon(centerButton, MaterialDesignIcon.IMAGE_FILTER_CENTER_FOCUS);
    setButtonIcon(powerButton, MaterialDesignIcon.POWER);
  }
  
}
