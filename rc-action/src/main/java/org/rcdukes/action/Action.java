package org.rcdukes.action;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;
import org.rcdukes.common.Characters;
import org.rcdukes.common.Config;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.Environment;
import org.rcdukes.common.Events;
import org.rcdukes.error.ErrorHandler;

import rx.Observable;
import rx.Subscription;

/**
 * action (aka Luke) control Verticle
 *
 */
public class Action extends DukesVerticle {

    private Subscription laneDetection;
    private Subscription stoppingZoneDetection;
    private Subscription startLightDetection;
    
    /**
     * construct me
     */
    public Action() {
      super(Characters.LUKE);
    }

    @Override
    public void start() throws Exception {
      super.preStart();
      consumer(Events.START_DRAG_NAVIGATION, x -> startDragNavigator());
      consumer(Events.STOP_NAVIGATION,x -> stopNavigator());
      super.postStart();
    }

    private void stopNavigator() {
        LOG.info("Stopping navigator");

        if (laneDetection != null) {
            laneDetection.unsubscribe();
        }

        if (startLightDetection != null) {
            startLightDetection.unsubscribe();
        }

        if (stoppingZoneDetection != null) {
            stoppingZoneDetection.unsubscribe();
        }

    }

    private void startDragNavigator() {
        LOG.info("Starting drag navigator");
        Environment env = Environment.getInstance();
        String wheelOrientation="+";
        try {
          wheelOrientation = env.getString(Config.WHEEL_ORIENTATION);
        } catch (Exception e) {
          ErrorHandler.getInstance().handle(e);
        }
        StartLightObserver startLightObserver = new StartLightObserver();
        StraightLaneNavigator straighLaneNavigator = new StraightLaneNavigator(wheelOrientation);
        StoppingZoneDetector stoppingZoneDetector = new StoppingZoneDetector();

        // failsafe ?
        laneDetection = vertx.eventBus().consumer(Events.LANE_DETECTED.name()).toObservable()
//                .doOnNext(evt -> LOG.trace("Received lane detection event (straight lane navigator): {}", evt.body()))
                .map(Message::body)
                .cast(String.class)
                .map(JsonObject::new)
                .flatMap(straighLaneNavigator::navigate)
                // failsafe ?
                .switchIfEmpty(Observable.just(new JsonObject().put("speed", "stop")))
                .subscribe(
                        instruction -> vertx.eventBus().publish(Characters.BO.getCallsign(), instruction),
                        error -> {
                            LOG.error("Error navigating, stopping", error);
                            vertx.eventBus().publish(Characters.BO.getCallsign(), new JsonObject().put("type", "motor").put("speed", "stop"));
                        },
                        () -> LOG.info("Completed navigating"));

        /*
        stoppingZoneDetection = vertx.eventBus().consumer(Events.LANEDETECTION.name()).toObservable()
//                .doOnNext(evt -> LOG.trace("Received lane detection event (stopping zone detector): {}", evt.body()))
                .map(Message::body)
                .cast(String.class)
                .map(JsonObject::new)
                .flatMap(stoppingZoneDetector::detect)
                .subscribe(
                        instruction -> vertx.eventBus().publish(Characters.BO.getCallsign(), instruction),
                        error -> LOG.error("Error stopping zone detection", error),
                        () -> LOG.info("Completed stopping zone detection"));
        */
        startLightDetection = vertx.eventBus().consumer(Events.STARTLIGHT_DETECTED.name()).toObservable()
                .doOnNext(evt -> LOG.trace("Received start light detection event: {}", evt))
                .map(Message::body)
                .cast(String.class)
                .map(JsonObject::new)
                .flatMap(startLightObserver::observe)
                .subscribe(instruction -> vertx.eventBus().publish(Characters.BO.getCallsign(), instruction));
        LOG.info("drag navigator started");
    }
}
