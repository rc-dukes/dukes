package org.rcdukes.app;

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
    String bgColor="transparent";
    String color="#808080";
    setButtonIcon(upButton,MaterialDesignIcon.ARROW_UP_BOLD,color,bgColor);
    setButtonIcon(autoPilotButton,MaterialDesignIcon.CAR,color,bgColor);
    setButtonIcon(leftButton,MaterialDesignIcon.ARROW_LEFT_BOLD,color,bgColor);
    setButtonIcon(stopButton,MaterialDesignIcon.STOP,color,bgColor);
    setButtonIcon(rightButton,MaterialDesignIcon.ARROW_RIGHT_BOLD,color,bgColor);
    setButtonIcon(downButton,MaterialDesignIcon.ARROW_DOWN_BOLD,color,bgColor);
    setButtonIcon(manualButton,MaterialDesignIcon.ACCOUNT,color,bgColor);
    setButtonIcon(brakeButton, MaterialDesignIcon.CLOSE,color,bgColor);
    setButtonIcon(centerButton, MaterialDesignIcon.IMAGE_FILTER_CENTER_FOCUS,color,bgColor);
    setButtonIcon(powerButton, MaterialDesignIcon.POWER,"blue",bgColor);
  }
  
}
