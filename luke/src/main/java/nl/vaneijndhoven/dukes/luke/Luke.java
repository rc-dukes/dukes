package nl.vaneijndhoven.dukes.luke;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import nl.vaneijndhoven.dukes.hazardcounty.Characters;
import nl.vaneijndhoven.dukes.hazardcounty.Events;
import nl.vaneijndhoven.dukes.luke.drag.StoppingZoneDetector;
import nl.vaneijndhoven.dukes.luke.drag.StraightLaneNavigator;
import nl.vaneijndhoven.dukes.luke.drag.StartLightObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscription;

public class Luke extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(Luke.class);
    public static final String START_DRAG_NAVIGATION = "START_DRAG_NAVIGATION";
    public static final String STOP_NAVIGATION = "STOP_NAVIGATION";
    private Subscription laneDetection;
    private Subscription stoppingZoneDetection;
    private Subscription startLightDetection;

    @Override
    public void start() throws Exception {
        LOG.info("Starting Luke (Hardcoded Intelligence?)");

        vertx.eventBus().consumer(Characters.LUKE.getCallsign() + ":" + START_DRAG_NAVIGATION, x -> startDragNavigator());
        vertx.eventBus().consumer(Characters.LUKE.getCallsign() + ":" + STOP_NAVIGATION, x -> stopNavigator());
    }

    private void stopNavigator() {
        LOG.info("Stopping navigator");

        laneDetection.unsubscribe();
        startLightDetection.unsubscribe();
        stoppingZoneDetection.unsubscribe();
    }

    private void startDragNavigator() {
        LOG.info("Starting drag navigator");

        StartLightObserver startLightObserver = new StartLightObserver();
        StraightLaneNavigator straighLaneNavigator = new StraightLaneNavigator();
        StoppingZoneDetector stoppingZoneDetector = new StoppingZoneDetector();

        // failsafe ?
        laneDetection = vertx.eventBus().consumer(Events.LANEDETECTION.name()).toObservable()
//                .doOnNext(evt -> LOG.trace("Received lane detection event (straight lane navigator): {}", evt.body()))
                .map(Message::body)
                .cast(String.class)
                .map(JsonObject::new)
                .flatMap(straighLaneNavigator::navigate)
                // failsafe ?
                .switchIfEmpty(Observable.just(new JsonObject().put("speed", "stop")))
                .subscribe(
                        instruction -> vertx.eventBus().publish(Characters.BO.getCallsign(), instruction),
                        error -> LOG.error("Error navigating", error),
                        () -> LOG.info("Completed navigating"));

        stoppingZoneDetection = vertx.eventBus().consumer(Events.LANEDETECTION.name()).toObservable()
                .doOnNext(evt -> LOG.trace("Received lane detection event (stopping zone detector): {}", evt.body()))
                .map(Message::body)
                .cast(String.class)
                .map(JsonObject::new)
                .flatMap(stoppingZoneDetector::detect)
                .subscribe(
                        instruction -> vertx.eventBus().publish(Characters.BO.getCallsign(), instruction),
                        error -> LOG.error("Error stopping zone detection", error),
                        () -> LOG.info("Completed stopping zone detection"));

        startLightDetection = vertx.eventBus().consumer(Events.STARTLIGHTDETECTION.name()).toObservable()
                .doOnNext(evt -> LOG.trace("Received start light detection event: {}", evt))
                .map(Message::body)
                .cast(String.class)
                .map(JsonObject::new)
                .flatMap(startLightObserver::observe)
                .subscribe(instruction -> vertx.eventBus().publish(Characters.BO.getCallsign(), instruction));

        LOG.info("Luke started");
    }

}
