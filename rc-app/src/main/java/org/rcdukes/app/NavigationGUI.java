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
    setButtonIcon(upButton, MaterialDesignIcon.ARROW_UP_BOLD);
    setButtonIcon(autoPilotButton, MaterialDesignIcon.UPLOAD);
    setButtonIcon(leftButton, MaterialDesignIcon.ARROW_LEFT_BOLD);
    setButtonIcon(stopButton, MaterialDesignIcon.STOP);
    setButtonIcon(rightButton, MaterialDesignIcon.ARROW_RIGHT_BOLD);
    setButtonIcon(downButton, MaterialDesignIcon.ARROW_DOWN_BOLD);
    setButtonIcon(manualButton, MaterialDesignIcon.ACCOUNT);
    setButtonIcon(brakeButton, MaterialDesignIcon.CLOSE);
    setButtonIcon(centerButton, MaterialDesignIcon.IMAGE_FILTER_CENTER_FOCUS);
    setButtonIcon(powerButton, MaterialDesignIcon.POWER);
    setButtonIcon(photoButton, MaterialDesignIcon.CAMERA);
    setButtonIcon(recordButton, MaterialDesignIcon.RECORD);
    setButtonIcon(startCarButton, MaterialDesignIcon.CAR);
    setButtonIcon(requestConfigButton, MaterialDesignIcon.SETTINGS);
    setButtonIcon(echoButton, MaterialDesignIcon.BULLHORN);
  }

  boolean power=false;
  
  @FXML
  private void onPower(final ActionEvent event) {
    power=!power;
    super.setButtonActive(powerButton,power);
  }
}
