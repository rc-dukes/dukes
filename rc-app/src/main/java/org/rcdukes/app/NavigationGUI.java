package org.rcdukes.app;

import org.rcdukes.common.Characters;
import org.rcdukes.common.Config;
import org.rcdukes.common.Events;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import io.vertx.core.json.JsonObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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
  
  EventbusLogger eventbusLogger;

  public EventbusLogger getEventbusLogger() {
    return eventbusLogger;
  }

  public void setEventbusLogger(EventbusLogger eventbusLogger) {
    this.eventbusLogger = eventbusLogger;
  }

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
    setControlState(power);
  }

  public void setControlState(boolean enable) {
    String color = enable ? "blue" : "grey";
    Button[] buttons = { manualButton, autoPilotButton, leftButton, rightButton,
        upButton, downButton, centerButton, stopButton, brakeButton,
        photoButton, recordButton };
    for (Button button : buttons) {
      super.setButtonColor(button, color, buttonBgColor);
      button.setDisable(!enable);
    }
  }

  boolean power = false;
  private AppVerticle appVerticle;

  /**
   * handle navigation key events
   * 
   * @param evt
   *          - the key event to handle
   */
  public void handleNavigationKey(KeyEvent evt) {
    KeyCode code = evt.getCode();
    switch (code) {
    case UP:
      up();
      break;
    case DOWN:
      down();
      break;
    case LEFT:
      left();
      break;
    case RIGHT:
      right();
      break;
    case SPACE:
      stop();
      break;
    case PLUS:
      autopilot();
      break;
    case MINUS:
      manual();
      break;
    default:
    }
  }

  @FXML
  private void onPower(final ActionEvent event) {
    power = !power;
    super.setButtonActive(powerButton, power);
    if (power) {
      appVerticle = getAppVerticle();
      super.setButtonActive(requestConfigButton, false);
      super.setButtonActive(startCarButton, false);
    } else {
      setControlState(false);
    }
    appVerticle.heartBeat(power);
  }

  @FXML
  private void onManual(final ActionEvent event) {
    manual();
  }

  private void manual() {
    if (!manualButton.isDisabled()) {
      super.setButtonActive(autoPilotButton, false);
      super.setButtonActive(manualButton, true);
    }
  }
  
  @FXML void onAutoPilot(final ActionEvent event) {
    autopilot();
  }
  
  private void autopilot() {
    if (!autoPilotButton.isDisabled()) {
      super.setButtonActive(autoPilotButton, true);
      super.setButtonActive(manualButton, false);
    }
  }

  @FXML
  private void onUp(final ActionEvent event) {
    up();
  }

  private void up() {
    if (!upButton.isDisabled())
      sendSpeedCommand("up");
  }

  @FXML
  private void onDown(final ActionEvent event) {
    down();
  }

  private void down() {
    if (!downButton.isDisabled())
      sendSpeedCommand("down");
  }

  @FXML
  private void onLeft(final ActionEvent event) {
    left();
  }

  private void left() {
    if (!leftButton.isDisabled())
      sendWheelCommand("left");
  }

  @FXML
  private void onRight(final ActionEvent event) {
    right();
  }

  private void right() {
    if (!rightButton.isDisabled())
      sendWheelCommand("right");
  }

  @FXML
  private void onStop(final ActionEvent event) {
    stop();
  }

  private void stop() {
    if (!stopButton.isDisabled()) {
      sendSpeedCommand("stop");
      sendWheelCommand("center");
    }
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
    JsonObject configJo = Config.getEnvironment().asJsonObject();
    getAppVerticle().sendEvent(Characters.GENERAL_LEE,
        Events.START_CAR, configJo);
    super.setButtonActive(startCarButton, true);
    setControlState(true);
  }

  @FXML
  private void onRequestConfig(final ActionEvent event) {
    getAppVerticle().sendEvent(Characters.BOARS_NEST,
        Events.REQUEST_CONFIG, null);
    super.setButtonActive(requestConfigButton, true);
  }

  /**
   * send the given speed command
   * 
   * @param speed
   */
  private void sendSpeedCommand(String speed) {
    getAppVerticle().sendSpeedCommand(speed);
  }

  /**
   * send the given wheel command
   * 
   * @param position
   */
  private void sendWheelCommand(String position) {
    getAppVerticle().sendWheelCommand(position);
  }
  
  /**
   * get the AppVerticle
   * @return - the singleton
   */
  protected AppVerticle getAppVerticle() {
    AppVerticle appVerticle=AppVerticle.getInstance(eventbusLogger);
    return appVerticle;
  }
}
