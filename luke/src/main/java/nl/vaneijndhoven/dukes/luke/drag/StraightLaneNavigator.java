package nl.vaneijndhoven.dukes.luke.drag;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import nl.vaneijndhoven.dukes.hazardcounty.Characters;
import rx.Observable;
import rx.exceptions.Exceptions;

import java.util.Map;

import static rx.Observable.just;
import static rx.exceptions.Exceptions.propagate;

public class StraightLaneNavigator {

    private static final long MAX_DURATION_NO_LINES_DETECTED = 1000;

    private long COMMAND_LOOP_INTERVAL = 100L;

    private long tsLastLinesDetected = System.currentTimeMillis();

    private double previousAngle = 0;

    private long tsLastCommand = System.currentTimeMillis();
    private long tsLastConnectionOKmessageSent = System.currentTimeMillis();
    private double lastRudderPercentageSent = 0d;

    private boolean emergencyStopActivated = false;

    public StraightLaneNavigator() {
        initDefaults();
    }

    private void initDefaults() {
        tsLastCommand = System.currentTimeMillis();
        lastRudderPercentageSent = 0d;
    }

    public Observable<JsonObject> navigate(JsonObject laneDetectResult) {
        return processLane(laneDetectResult)
                .onErrorResumeNext(throwable -> {
                    // Convert No Lines Detected situation into stop command.
                    if (throwable instanceof NoLinesDetected) {
                        emergencyStopActivated = true;
                        JsonObject message = new JsonObject().put("type", "motor").put("speed", "stop");
                        return just(message);
                    } else {
                        return Observable.error(propagate(throwable));
                    }
                });

    }


    private Observable<JsonObject> processLane(JsonObject laneDetectResult) throws NoLinesDetected {
        long currentTime = System.currentTimeMillis();

        Double angle = null;
        if (laneDetectResult.containsKey("angle")) {
            angle = laneDetectResult.getDouble("angle");
        }

        Double courseRelativeToHorizon = null;
        if (laneDetectResult.containsKey("courseRelativeToHorizon")) {
            courseRelativeToHorizon = laneDetectResult.getDouble("courseRelativeToHorizon");
        }

        verifyAngleFound(angle, currentTime);

        if (angle == null) {
            return Observable.empty();
        }



        // steer on courseRelativeToHorizon
        if (courseRelativeToHorizon != null) {
            if (currentTime - tsLastCommand > COMMAND_LOOP_INTERVAL) {

                System.out.println("steering based on courseRelativeToHorizon: " + courseRelativeToHorizon);
                double rudderPercentage = courseRelativeToHorizon * -1.0;

                tsLastCommand = currentTime;
                lastRudderPercentageSent = rudderPercentage;
                previousAngle = angle;

                JsonObject message = new JsonObject().put("type", "servoDirect").put("position", String.valueOf(rudderPercentage));

                return just(message);
            }
        }




        // fallback: steer on angle
        double rudderPercentage = 100 * (Math.abs(angle) / 60) * 0.45; // 0.5 voor rechtdoor rijden

        if (currentTime - tsLastCommand > COMMAND_LOOP_INTERVAL) {
            if (
                    (previousAngle != 0) &&
                            (!((previousAngle < 0 && angle > 0) || (previousAngle > 0 && angle < 0)))
                            && (Math.abs(angle) < Math.abs(previousAngle))) {
                System.out.println("center");
                rudderPercentage = 0;
            } else {
                if (rudderPercentage > 0) {
                    String direction;
                    if (angle > 0) {
                        direction = "left";
                        rudderPercentage = -rudderPercentage * 1;
                    } else {
                        direction = "right";
                        rudderPercentage = rudderPercentage * 1.3;
                    }
                }


            }
            tsLastCommand = currentTime;
            lastRudderPercentageSent = rudderPercentage;
            previousAngle = angle;

            JsonObject message = new JsonObject().put("type", "servoDirect").put("position", String.valueOf(rudderPercentage));

            return just(message);
        }

        return Observable.empty();
    }

    /// TODO rewrite to throw exception so calling class can act on specific error to send stop command.
    private void verifyAngleFound(Double angle, long currentTime) throws NoLinesDetected {
        if (angle == null) {
            // no angle detected
            if ((currentTime - tsLastLinesDetected > MAX_DURATION_NO_LINES_DETECTED) && !emergencyStopActivated) {
                System.out.println("no angle found for " + MAX_DURATION_NO_LINES_DETECTED + "ms, emergency stop");
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
