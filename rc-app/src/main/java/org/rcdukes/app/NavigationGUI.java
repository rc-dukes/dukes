package org.rcdukes.app;

import org.rcdukes.common.Characters;
import org.rcdukes.common.Config;
import org.rcdukes.common.Events;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import io.vertx.core.json.JsonObject;
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

  boolean power = false;
  private AppVerticle appVerticle;

  @FXML
  private void onPower(final ActionEvent event) {
    power = !power;
    super.setButtonActive(powerButton, power);
    if (power) {
      appVerticle = AppVerticle.getInstance();
      super.setButtonActive(requestConfigButton,false);
      super.setButtonActive(startCarButton,false);
    }
    appVerticle.heartBeat(power);
  }

  @FXML
  private void onUp(final ActionEvent event) {
    sendSpeedCommand("up");
  }

  @FXML
  private void onDown(final ActionEvent event) {
    sendSpeedCommand("down");
  }

  @FXML
  private void onLeft(final ActionEvent event) {
    sendWheelCommand("left");
  }

  @FXML
  private void onRight(final ActionEvent event) {
    sendWheelCommand("right");
  }

  @FXML
  private void onStop(final ActionEvent event) {
    sendSpeedCommand("stop");
    sendWheelCommand("center");
  }

  @FXML
  private void onBrake(final ActionEvent event) {
    sendSpeedCommand("brake");
  }

  @FXML
  private void onCenter(final ActionEvent event) {
    sendWheelCommand("center");
  }
  
  @FXML
  private void onStartCar(final ActionEvent event) throws Exception {
    // @TODO - receive config from BOARS_NEST?
    JsonObject configJo=Config.getEnvironment().asJsonObject();
    AppVerticle.getInstance().sendEvent(Characters.GENERAL_LEE, Events.START_CAR,configJo);
    super.setButtonActive(startCarButton, true);
  }
  
  @FXML
  private void onRequestConfig(final ActionEvent event) {
    AppVerticle.getInstance().sendEvent(Characters.BOARS_NEST,Events.REQUEST_CONFIG, null);
    super.setButtonActive(requestConfigButton, true);
  }

  /**
   * send the given speed command
   * 
   * @param speed
   */
  private void sendSpeedCommand(String speed) {
    AppVerticle.getInstance().sendSpeedCommand(speed);
  }

  /**
   * send the given wheel command
   * 
   * @param position
   */
  private void sendWheelCommand(String position) {
    AppVerticle.getInstance().sendWheelCommand(position);
  }
}
