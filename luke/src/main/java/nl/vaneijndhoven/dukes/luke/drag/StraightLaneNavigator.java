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
//    private boolean stoppingZoneDetected = false;
    //    private Vertx vertx;
    private long tsLastLinesDetected = System.currentTimeMillis();

    private double previousAngle = 0;

//    private double minDistanceToStoppingZone = Double.MAX_VALUE;
    private long tsLastCommand = System.currentTimeMillis();
    private long tsLastConnectionOKmessageSent = System.currentTimeMillis();
    private double lastRudderPercentageSent = 0d;

    private boolean emergencyStopActivated = false;

    public StraightLaneNavigator() {
        initDefaults();
    }

    private void initDefaults() {
//        minDistanceToStoppingZone = Double.MAX_VALUE;
        tsLastCommand = System.currentTimeMillis();
        lastRudderPercentageSent = 0d;
    }

    public Observable<String> navigate(JsonObject laneDetectResult) {
        double distanceToStoppingZoneStart = laneDetectResult.getDouble("distanceToStoppingZone");
        double distanceToStoppingZoneEnd = laneDetectResult.getDouble("distanceToStoppingZoneEnd");
        double angle = laneDetectResult.getDouble("angle");

        return processLane(angle, distanceToStoppingZoneStart, distanceToStoppingZoneEnd)
                .onErrorReturn(
                    throwable -> {
                        // Convert No Lines Detected situation into stop command.
                        if (throwable instanceof NoLinesDetected) {
                            emergencyStopActivated = true;
                            return new JsonObject().put("speed","stop").encode();
                        } else {
                            propagate(throwable);
                            return null;
                        }
                    });
    }


    private Observable<String> processLane(double angle, double distanceToStoppingZoneStart, double distanceToStoppingZoneEnd) throws NoLinesDetected {
        long currentTime = System.currentTimeMillis();

//        logConnectionOK();
//        Observable<String> stoppingZoneObservable = detectStoppingZone(distanceToStoppingZoneStart);
//        return stoppingZoneObservable.switchIfEmpty(Observable.defer(() -> {
            verifyAngleFound(angle, currentTime);

            double rudderPercentage = 100 * (Math.abs(angle) / 60) * 0.45; // 0.5 voor rechtdoor rijden
    //        double damp = Math.abs(previousAngle)   - angle);
            // System.out.println("prev: " + previousAngle + ", angle: " + angle + ", rudderPercentage: " + rudderPercentage + ", damp: " + damp);

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

                        // System.out.println("steering " + direction + ", percentage: " + rudderPercentage);

                    }


                }
                tsLastCommand = currentTime;
                lastRudderPercentageSent = rudderPercentage;
                previousAngle = angle;

                return just(new JsonObject().put("setwheel", rudderPercentage).encode());
    //            eventBusSendAfterMS(0, "setwheel:" + rudderPercentage);


            }

            return Observable.empty();
//        }));
    }

    /// TODO rewrite to throw exception so calling class can act on specific error to send stop command.
    private void verifyAngleFound(double angle, long currentTime) throws NoLinesDetected {
        if (!(angle > 0) && !(angle < 0)) {
            // no angle detected
            if ((currentTime - tsLastLinesDetected > MAX_DURATION_NO_LINES_DETECTED) && !emergencyStopActivated) {
                System.out.println("no angle found for " + MAX_DURATION_NO_LINES_DETECTED + "ms, emergency stop");
//                    return "speed:stop";
//                    vertx.eventBus().send("control", "speed:stop");
                    throw new NoLinesDetected();
//                if (vertx != null) {
//                        emergencyStopActivated = true;
            }
        } else {
            // all good
            tsLastLinesDetected = currentTime;
        }
    }

//    private void eventBusSendAfterMS(long ms, String command) {
//        new java.util.Timer().schedule(
//                new java.util.TimerTask() {
//                    @Override
//                    public void run() {
////                        System.out.println("Sending command '" + command + "'.");
//                        if (vertx != null) {
//                            vertx.eventBus().send("control", command);
//                        } else {
//                            System.out.println("Couldn't send command '" + command + "', Vert.x not inited");
//                        }
//                    }
//                },
//                ms
//        );
//    }


//    private void logConnectionOK() {
//        int logConnectionOKinterval = 1000;
//
//        long currentTime = System.currentTimeMillis();
//        if (currentTime - tsLastConnectionOKmessageSent > logConnectionOKinterval) {
//            eventBusSendAfterMS(0, "log:LaneDetectionController connected");
//            tsLastConnectionOKmessageSent = currentTime;
//        }
//
//    }

    private class NoLinesDetected extends RuntimeException {
    }
}
