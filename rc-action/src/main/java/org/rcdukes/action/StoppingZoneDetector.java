package org.rcdukes.action;

import io.vertx.core.json.JsonObject;
import rx.Observable;

import java.util.concurrent.TimeUnit;

import static rx.Observable.just;

/**
 * stopping zone detector
 */
public class StoppingZoneDetector {

    private boolean stoppingZoneDetected = false;
    private double minDistanceToStoppingZone = Double.MAX_VALUE;

    public Observable<JsonObject> detect(JsonObject laneDetectResult) {
        if (!laneDetectResult.containsKey("distanceToStoppingZone")) {
            return Observable.empty();
        }

        double distanceToStoppingZoneStart = laneDetectResult.getDouble("distanceToStoppingZone");
//        double distanceToStoppingZoneEnd = laneDetectResult.getDouble("distanceToStoppingZoneEnd");

        return detectStoppingZone(distanceToStoppingZoneStart);
    }

    private Observable<JsonObject> detectStoppingZone(double distanceToStoppingZoneStart) {

        if (distanceToStoppingZoneStart > 0) {

            if (distanceToStoppingZoneStart < minDistanceToStoppingZone) {
                minDistanceToStoppingZone = distanceToStoppingZoneStart;
                System.out.println("new minimal distance to stopping zone: " + minDistanceToStoppingZone);
            }

            if (minDistanceToStoppingZone < 100) {
                if (distanceToStoppingZoneStart - minDistanceToStoppingZone > 30) {
                    if (!stoppingZoneDetected) {
                        System.out.println("--- stop ---");
                        stoppingZoneDetected = true;
                        JsonObject message = new JsonObject().put("type", "motor").put("speed", "brake");
                        return just(message).delay(1000, TimeUnit.MILLISECONDS);
                    }
                }
            }
        }

        return Observable.empty();
//            if (distanceToStoppingZoneStart - minDistanceToStoppingZone > 20) {
//                System.out.println("--- stop ---");
//                eventBusSendAfterMS(10,"speed:brake");
//            }


//            System.out.println("distanceToStoppingZoneStart: " + distanceToStoppingZoneStart);
//
//            if (distanceToStoppingZoneStart > 5 && distanceToStoppingZoneStart < 15) {
//                System.out.println("--- stop ---");
//                eventBusSendAfterMS(10,"speed:brake");
//            }

//            if (distanceToStoppingZoneStart < minDistanceToStoppingZone) {
//                minDistanceToStoppingZone = distanceToStoppingZoneStart;
//                System.out.println("new minimal distance to stopping zone: " + minDistanceToStoppingZone);
//            }
//
//            if (Math.abs(distanceToStoppingZoneStart - minDistanceToStoppingZone) > 10) {
//                System.out.println("distance increasing: min=" + minDistanceToStoppingZone + ", cur = " + distanceToStoppingZoneStart);
//
//                int wait = 500;
//                stoppingZoneDetected = true;
//                System.out.println("stopping zone detected, stopping in " + wait + " ms");
//                // eventBusSendAfterMS(wait,"speed:brake");
//            }
//        }
    }
}
