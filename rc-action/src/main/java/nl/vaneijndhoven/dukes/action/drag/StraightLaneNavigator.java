package nl.vaneijndhoven.dukes.action.drag;

import static rx.Observable.just;
import static rx.exceptions.Exceptions.propagate;

import io.vertx.core.json.JsonObject;
import rx.Observable;
import stormbots.MiniPID;

/**
 * straight line navigator
 *
 */
public class StraightLaneNavigator {

  private static final long MAX_DURATION_NO_LINES_DETECTED = 1000;

  // private long COMMAND_LOOP_INTERVAL = 250L;
  private long COMMAND_LOOP_INTERVAL = 50L;

  private long tsLastLinesDetected = System.currentTimeMillis();

  private double previousAngle = 0;

  private long tsLastCommand = System.currentTimeMillis();
  private long tsLastConnectionOKmessageSent = System.currentTimeMillis();
  private double lastRudderPercentageSent = 0d;

  private boolean emergencyStopActivated = false;

  public StraightLaneNavigator() {
    initDefaults();
  }

  MiniPID pid;

  private void initDefaults() {
    tsLastCommand = System.currentTimeMillis();
    lastRudderPercentageSent = 0d;
    pid = new MiniPID(1, 0, 0);
  }

  public Observable<JsonObject> navigate(JsonObject laneDetectResult) {
    return processLane(laneDetectResult).onErrorResumeNext(throwable -> {
      // Convert No Lines Detected situation into stop command.
      if (throwable instanceof NoLinesDetected) {
        emergencyStopActivated = true;
        JsonObject message = new JsonObject().put("type", "motor").put("speed",
            "stop");
        return just(message);
      } else {
        return Observable.error(propagate(throwable));
      }
    });

  }

  private Observable<JsonObject> processLane(JsonObject laneDetectResult)
      throws NoLinesDetected {
    long currentTime = System.currentTimeMillis();

    Double angle = null;
    if (laneDetectResult.containsKey("angle")) {
      angle = laneDetectResult.getDouble("angle");
    }

    Double courseRelativeToHorizon = null;
    if (laneDetectResult.containsKey("courseRelativeToHorizon")) {
      courseRelativeToHorizon = laneDetectResult
          .getDouble("courseRelativeToHorizon");
    }

    verifyAngleFound(angle, currentTime);

    if (angle == null) {
      return Observable.empty();
    }

    Double rudderPercentage;

    if (courseRelativeToHorizon != null) {
      // pass 1: steer on courseRelativeToHorizon
      rudderPercentage = courseRelativeToHorizon * -1.0;
      // System.out.println("rudder on horizon: " + rudderPercentage);
    } else {
      // pass 2: steer on angle
      if (angle < 0) {
        // left
        rudderPercentage = 4 * angle;
      } else {
        // right
        rudderPercentage = 6 * angle;
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
      previousAngle = angle;
      JsonObject message = new JsonObject().put("type", "servoDirect")
          .put("position", String.valueOf(rudderPercentage));
      return just(message);
    }

    return Observable.empty();
  }

  /// TODO rewrite to throw exception so calling class can act on specific error
  /// to send stop command.
  private void verifyAngleFound(Double angle, long currentTime)
      throws NoLinesDetected {
    if (angle == null) {
      // no angle detected
      if ((currentTime - tsLastLinesDetected > MAX_DURATION_NO_LINES_DETECTED)
          && !emergencyStopActivated) {
        System.out.println("no angle found for "
            + MAX_DURATION_NO_LINES_DETECTED + "ms, emergency stop");
        throw new NoLinesDetected();
      }
    } else {
      // all good
      tsLastLinesDetected = currentTime;
    }
  }

  private class NoLinesDetected extends RuntimeException {
  }
}
