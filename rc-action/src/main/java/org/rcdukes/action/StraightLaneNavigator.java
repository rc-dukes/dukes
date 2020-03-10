package org.rcdukes.action;

import java.util.Locale;

import org.rcdukes.common.Characters;
import org.rcdukes.common.Config;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.Environment;
import org.rcdukes.error.ErrorHandler;
import org.rcdukes.geometry.LaneDetectionResult;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.Message;
import stormbots.MiniPID;

/**
 * straight lane navigator
 *
 */
public class StraightLaneNavigator implements Navigator {

  boolean debug = false;
  protected static final Logger LOG = LoggerFactory
      .getLogger(StraightLaneNavigator.class);
  DukesVerticle sender;

  public DukesVerticle getSender() {
    return sender;
  }

  public void setSender(DukesVerticle sender) {
    this.sender = sender;
  }

  public String getWheelOrientationFromEnvironment() {
    Environment env = Environment.getInstance();
    String wheelOrientation = "+";
    try {
      wheelOrientation = env.getString(Config.WHEEL_ORIENTATION);
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }
    return wheelOrientation;
  }

  /**
   * construct me with the given wheel orientation
   * 
   * @param wheelOrientation
   */
  public StraightLaneNavigator(String wheelOrientation) {
    if (wheelOrientation == null)
      wheelOrientation = this.getWheelOrientationFromEnvironment();
    this.wheelOrientation = wheelOrientation;
    initDefaults();
  }

  /**
   * default constructor
   */
  public StraightLaneNavigator() {
    this(null);
  }

  MiniPID pid;
  private String wheelOrientation;
  private int rudderFactor;
  private Long tsLastLinesDetected;
  private Long tsLastCommand;
  public static long COMMAND_LOOP_INTERVAL = 200L; // how often to send commands
  private static final long MAX_DURATION_NO_LINES_DETECTED = 1000;
  private boolean emergencyStopActivated = false;
  
  /**
   * initialize my defaults
   */
  private void initDefaults() {
    pid = new MiniPID(1, 0, 0);
    rudderFactor = 1;
    if (wheelOrientation.equals("-"))
      rudderFactor = -1;
  }

  @Override
  public LaneDetectionResult fromJsonObject(JsonObject jo) {
    LaneDetectionResult ldr = jo.mapTo(LaneDetectionResult.class);
    return ldr;
  }

  enum AngleCheck {
    ok, empty, noLines
  }

  /**
   * check the angle
   * 
   * @param ldr
   * @param currentTime
   * @return the AngleCheck
   */
  private AngleCheck verifyAngleFound(LaneDetectionResult ldr,
      long currentTime) {
    AngleCheck result = AngleCheck.ok;
    if (ldr.middle == null && ldr.left == null && ldr.right == null) {
      // no angle detected
      if ((currentTime - tsLastLinesDetected > MAX_DURATION_NO_LINES_DETECTED)
          && !emergencyStopActivated) {
        String msg = String.format("no angle found for %d ms, emergency stop",
            MAX_DURATION_NO_LINES_DETECTED);
        LOG.warn(msg);
        result = AngleCheck.noLines;
      }
      result = AngleCheck.empty;
    } else {
      // all good
      tsLastLinesDetected = currentTime;
    }
    return result;
  }
  
  /**
   * process the laneDetectResult
   * 
   * @param ldr
   *          - the lane detection result
   * @return - a message to be sent to the vehicle or null on error
   */
  @Override
  public JsonObject getNavigationInstruction(LaneDetectionResult ldr) {
    long currentTime = ldr.milliTimeStamp;
    if (tsLastLinesDetected==null) tsLastLinesDetected=currentTime;
    if (tsLastCommand==null) tsLastCommand=currentTime;

    AngleCheck angleCheck = verifyAngleFound(ldr, currentTime);
    switch (angleCheck) {
    case empty:
      return null;
    case noLines:
      return ActionVerticle.emergencyStopCommand();
    default:
      // ok - do nothing
    }

    Double angle;

    if (ldr.courseRelativeToHorizon != null) {
      // pass 1: steer on courseRelativeToHorizon
      angle = Math.toDegrees(ldr.courseRelativeToHorizon) * rudderFactor;
      // System.out.println("rudder on horizon: " + rudderPercentage);
    } else {
      angle = null;
    }
    if (angle != null) {
      if (currentTime - tsLastCommand > COMMAND_LOOP_INTERVAL) {
        tsLastCommand = currentTime;
        JsonObject message = steerCommand(angle);
        return message;
      }
    }
    return null;
  }
  
  /**
   * get the command to steer the vehicle
   * 
   * @param angle
   * @return - the command message
   */
  public JsonObject steerCommand(Double angle) {
    String angleStr = String.format(Locale.ENGLISH, "%5.1f°", angle);
    JsonObject message = new JsonObject().put("type", "servoAngle").put("angle",
        angle);
    String debugMsg = String.format("sending servoAngle %s", angleStr);
    if (debug)
      LOG.debug(debugMsg);
    return message;
  }

  @Override
  public void navigateWithInstruction(JsonObject navigationInstruction) {
    if (navigationInstruction != null)
      sender.send(Characters.BO, navigationInstruction);
  }

  @Override
  public void navigateWithMessage(Message<JsonObject> ldrMessage) {
    LaneDetectionResult ldr = fromJsonObject(ldrMessage.body());
    this.navigateWithLaneDetectionResult(ldr);
  }

  @Override
  public void navigateWithLaneDetectionResult(LaneDetectionResult ldr) {
    JsonObject navigationJo = getNavigationInstruction(ldr);
    navigateWithInstruction(navigationJo);
  }

}
