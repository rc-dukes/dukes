package nl.vaneijndhoven.dukes.luke.drag;

import io.vertx.core.json.JsonObject;
import rx.Observable;

import java.util.concurrent.TimeUnit;

import static rx.Observable.just;

public class StoppingZoneDetector {

    private boolean stoppingZoneDetected = false;
    private double minDistanceToStoppingZone = Double.MAX_VALUE;

    public Observable<String> detect(JsonObject laneDetectResult) {
        double distanceToStoppingZoneStart = laneDetectResult.getDouble("distanceToStoppingZone");
        double distanceToStoppingZoneEnd = laneDetectResult.getDouble("distanceToStoppingZoneEnd");

        return detectStoppingZone(distanceToStoppingZoneStart);
    }

    private Observable<String> detectStoppingZone(double distanceToStoppingZoneStart) {
        // System.out.println("distanceToStoppingZoneStart: " + distanceToStoppingZoneStart);

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
                        return just(new JsonObject().put("speed", "brake").encode()).delay(1000, TimeUnit.MILLISECONDS);
//                        eventBusSendAfterMS(1000,"speed:brake");
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
