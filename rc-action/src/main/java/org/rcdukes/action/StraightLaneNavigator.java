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
 * straight line navigator
 *
 */
public class StraightLaneNavigator implements Navigator {
  
  boolean debug=false;
  protected static final Logger LOG = LoggerFactory
      .getLogger(StraightLaneNavigator.class);
  private static final long MAX_DURATION_NO_LINES_DETECTED = 1000;

  // private long COMMAND_LOOP_INTERVAL = 250L;
  public static long COMMAND_LOOP_INTERVAL = 50L;

  private long tsLastLinesDetected = System.currentTimeMillis();

  private double previousAngle = 0;

  private long tsLastCommand = System.currentTimeMillis();
  private long tsLastConnectionOKmessageSent = System.currentTimeMillis();
  private double lastRudderPercentageSent = 0d;

  private boolean emergencyStopActivated = false;
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

  /**
   * initialize my defaults
   */
  private void initDefaults() {
    tsLastCommand = System.currentTimeMillis();
    lastRudderPercentageSent = 0d;
    pid = new MiniPID(1, 0, 0);
    rudderFactor = 1;
    if (wheelOrientation.equals("-"))
      rudderFactor = -1;
  }

  @Override
  public LaneDetectionResult fromJsonObject(JsonObject jo) {
    LaneDetectionResult ldr = new LaneDetectionResult();
    if (jo.containsKey("angle")) {
      ldr.angle = jo.getDouble("angle");
    }

    if (jo.containsKey("courseRelativeToHorizon")) {
      ldr.courseRelativeToHorizon = jo.getDouble("courseRelativeToHorizon");
    }
    return ldr;
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
    long currentTime = System.currentTimeMillis();

    AngleCheck angleCheck = verifyAngleFound(ldr.angle, currentTime);
    switch (angleCheck) {
    case empty:
      return null;
    case noLines:
      return Action.emergencyStopCommand();
    default:
      // ok - do nothing
    }

    Double rudderPercentage;

    if (ldr.courseRelativeToHorizon != null) {
      // pass 1: steer on courseRelativeToHorizon
      rudderPercentage = ldr.courseRelativeToHorizon * rudderFactor;
      // System.out.println("rudder on horizon: " + rudderPercentage);
    } else {
      // pass 2: steer on angle
      if (ldr.angle < 0) {
        // left
        rudderPercentage = 4 * ldr.angle;
      } else {
        // right
        rudderPercentage = 6 * ldr.angle;
      }
      // System.out.println("rudder on angle: " + rudderPercentage);

    }

    if (rudderPercentage > 100) {
      rudderPercentage = 100d;
    } else if (rudderPercentage < -100) {
      rudderPercentage = -100d;
    }

    if (currentTime - tsLastCommand > COMMAND_LOOP_INTERVAL) {

      // double pidRudderPercentage = pid.getOutput(angle * 5,
      // rudderPercentage);
      // System.out.println("rudderPercentage: " + rudderPercentage + ", pid
      // rudder percentage: " + pidRudderPercentage);
      // rudderPercentage = pidRudderPercentage;

      tsLastCommand = currentTime;
      lastRudderPercentageSent = rudderPercentage;
      previousAngle = ldr.angle;
      JsonObject message = steerCommand(rudderPercentage);
      return message;
    }

    return null;
  }

  /**
   * get the command to steer the vehicle
   * 
   * @param rudderPercentage
   * @return - the command message
   */
  public JsonObject steerCommand(Double rudderPercentage) {
    String rudderPos = String.format(Locale.ENGLISH, "%3.1f", rudderPercentage);
    JsonObject message = new JsonObject().put("type", "servoDirect")
        .put("position", rudderPos);
    String debugMsg = String.format("sending servoDirect position %3.1f",
        rudderPercentage);
    if (debug)
      LOG.debug(debugMsg);
    return message;
  }

  enum AngleCheck {
    ok, empty, noLines
  }

  /**
   * check the angle
   * 
   * @param angle
   * @param currentTime
   * @return the AngleCheck
   */
  private AngleCheck verifyAngleFound(Double angle, long currentTime) {
    AngleCheck result = AngleCheck.ok;
    if (angle == null) {
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

  @Override
  public void navigateWithInstruction(
      JsonObject navigationInstruction) {
    if (navigationInstruction!=null)
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
