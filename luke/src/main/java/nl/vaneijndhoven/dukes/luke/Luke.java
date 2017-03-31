package nl.vaneijndhoven.dukes.luke;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import nl.vaneijndhoven.dukes.hazardcounty.Characters;
import nl.vaneijndhoven.dukes.hazardcounty.Events;
import nl.vaneijndhoven.dukes.luke.drag.StoppingZoneDetector;
import nl.vaneijndhoven.dukes.luke.drag.StraightLaneNavigator;
import nl.vaneijndhoven.dukes.luke.drag.StartLightObserver;
import rx.Observable;

public class Luke extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        StartLightObserver startLightObserver = new StartLightObserver();
        StraightLaneNavigator straighLaneNavigator = new StraightLaneNavigator();
        StoppingZoneDetector stoppingZoneDetector = new StoppingZoneDetector();

        consumeEvents(Events.LANEDETECTION)
                .flatMap(straighLaneNavigator::navigate)
                .subscribe(instruction -> vertx.eventBus().send(Characters.BO.getCallsign(), instruction));

        consumeEvents(Events.LANEDETECTION)
                .flatMap(stoppingZoneDetector::detect)
                .subscribe(instruction -> vertx.eventBus().send(Characters.BO.getCallsign(), instruction));

        consumeEvents(Events.STARTLIGHTDETECTION)
                .flatMap(startLightObserver::observe)
                .subscribe(instruction -> vertx.eventBus().send(Characters.BO.getCallsign(), instruction));
    }

    private Observable<JsonObject> consumeEvents(Events event) {
        return vertx.eventBus().consumer(event.name()).toObservable()
                .map(Message::body)
                .cast(JsonObject.class);
    }
}
