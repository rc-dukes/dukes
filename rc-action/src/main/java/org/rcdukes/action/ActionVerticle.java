package org.rcdukes.action;

import org.rcdukes.common.Characters;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.Events;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Subscription;

/**
 * action (aka Luke) control Verticle
 *
 */
public class ActionVerticle extends DukesVerticle {

  private Subscription laneDetection;
  private Subscription stoppingZoneDetection;
  private Subscription startLightDetection;
  private Navigator navigator;

  /**
   * construct me
   */
  public ActionVerticle() {
    super(Characters.LUKE);
  }

  @Override
  public void start() throws Exception {
    super.preStart();
    consumer(Events.START_DRAG_NAVIGATION, x -> startDragNavigator());
    consumer(Events.STOP_NAVIGATION, x -> stopNavigator());
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

  /**
   * return an emergencyStopCommand
   * 
   * @return - the emergency stop command
   */
  public static JsonObject emergencyStopCommand() {
    JsonObject cmd = new JsonObject().put("type", "motor").put("speed", "stop");
    return cmd;
  }

  private void startDragNavigator() {
    LOG.info("Starting drag navigator");
    StartLightObserver startLightObserver = new StartLightObserver();
    navigator = new StraightLaneNavigator();
    navigator.setSender(this);
    StoppingZoneDetector stoppingZoneDetector = new StoppingZoneDetector();
    super.consumer(Events.LANE_DETECTED, navigator::navigateWithMessage);
    /*
     * stoppingZoneDetection =
     * vertx.eventBus().consumer(Events.LANEDETECTION.name()).toObservable() //
     * .doOnNext(evt ->
     * LOG.trace("Received lane detection event (stopping zone detector): {}",
     * evt.body())) .map(Message::body) .cast(String.class)
     * .map(JsonObject::new) .flatMap(stoppingZoneDetector::detect) .subscribe(
     * instruction -> vertx.eventBus().publish(Characters.BO.getCallsign(),
     * instruction), error -> LOG.error("Error stopping zone detection", error),
     * () -> LOG.info("Completed stopping zone detection"));
     */
    startLightDetection = vertx.eventBus()
        .consumer(Events.STARTLIGHT_DETECTED.name()).toObservable()
        .doOnNext(
            evt -> LOG.trace("Received start light detection event: {}", evt))
        .map(Message::body).cast(String.class).map(JsonObject::new)
        .flatMap(startLightObserver::observe).subscribe(instruction -> vertx
            .eventBus().publish(Characters.BO.getCallsign(), instruction));
    LOG.info("drag navigator started");
  }
}
