package org.rcdukes.app;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * navigation GUI
 * 
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
  protected Button photoButton;
  @FXML
  protected Button recordButton;
  @FXML
  protected Button startCarButton;
  @FXML
  protected Button requestConfigButton;
  @FXML
  protected Button echoButton;

  @FXML
  public void initialize() {
    setButtonIcon(upButton, MaterialDesignIcon.ARROW_UP_BOLD, buttonInactiveColor, buttonBgColor);
    setButtonIcon(autoPilotButton, MaterialDesignIcon.UPLOAD, buttonInactiveColor, buttonBgColor);
    setButtonIcon(leftButton, MaterialDesignIcon.ARROW_LEFT_BOLD, buttonInactiveColor,
        buttonBgColor);
    setButtonIcon(stopButton, MaterialDesignIcon.STOP, buttonInactiveColor, buttonBgColor);
    setButtonIcon(rightButton, MaterialDesignIcon.ARROW_RIGHT_BOLD, buttonInactiveColor,
        buttonBgColor);
    setButtonIcon(downButton, MaterialDesignIcon.ARROW_DOWN_BOLD, buttonInactiveColor,
        buttonBgColor);
    setButtonIcon(manualButton, MaterialDesignIcon.ACCOUNT, buttonInactiveColor, buttonBgColor);
    setButtonIcon(brakeButton, MaterialDesignIcon.CLOSE, buttonInactiveColor, buttonBgColor);
    setButtonIcon(centerButton, MaterialDesignIcon.IMAGE_FILTER_CENTER_FOCUS,
        buttonInactiveColor, buttonBgColor);
    setButtonIcon(powerButton, MaterialDesignIcon.POWER, buttonColor,buttonBgColor);
    setButtonIcon(photoButton, MaterialDesignIcon.CAMERA, buttonInactiveColor, buttonBgColor);
    setButtonIcon(recordButton, MaterialDesignIcon.RECORD, buttonInactiveColor, buttonBgColor);
    setButtonIcon(startCarButton, MaterialDesignIcon.CAR, buttonColor, buttonBgColor);
    setButtonIcon(requestConfigButton, MaterialDesignIcon.SETTINGS, buttonColor,
        buttonBgColor);
    setButtonIcon(echoButton, MaterialDesignIcon.BULLHORN, buttonColor, buttonBgColor);
  }

  boolean power=false;
  
  @FXML
  private void onPower(final ActionEvent event) {
    power=!power;
    super.setButtonActive(powerButton,power);
  }
}
