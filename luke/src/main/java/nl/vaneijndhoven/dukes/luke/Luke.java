package nl.vaneijndhoven.dukes.luke;

import com.fasterxml.jackson.databind.ObjectMapper;
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

public class Luke extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(Luke.class);

    @Override
    public void start() throws Exception {
        LOG.info("Starting Luke (Hardcoded Intelligence?)");
        StartLightObserver startLightObserver = new StartLightObserver();
        StraightLaneNavigator straighLaneNavigator = new StraightLaneNavigator();
        StoppingZoneDetector stoppingZoneDetector = new StoppingZoneDetector();

        vertx.eventBus().consumer(Events.LANEDETECTION.name()).toObservable()
                .doOnNext(evt -> LOG.trace("Received lane detection event (straight lane navigator): {}", evt))
                .map(Message::body)
                .cast(String.class)
                .map(JsonObject::new)
                .flatMap(straighLaneNavigator::navigate)
                .subscribe(instruction -> vertx.eventBus().publish(Characters.BO.getCallsign(), instruction));

        vertx.eventBus().consumer(Events.LANEDETECTION.name()).toObservable()
                .doOnNext(evt -> LOG.trace("Received lane detection event (stopping zone detector): {}", evt))
                .map(Message::body)
                .cast(String.class)
                .map(JsonObject::new)
                .flatMap(stoppingZoneDetector::detect)
                .subscribe(instruction -> vertx.eventBus().publish(Characters.BO.getCallsign(), instruction));

        vertx.eventBus().consumer(Events.STARTLIGHTDETECTION.name()).toObservable()
                .doOnNext(evt -> LOG.trace("Received start light detection event: {}", evt))
                .map(Message::body)
                .cast(String.class)
                .map(JsonObject::new)
                .flatMap(startLightObserver::observe)
                .subscribe(instruction -> vertx.eventBus().publish(Characters.BO.getCallsign(), instruction));

        LOG.info("Luke started");
    }

}
