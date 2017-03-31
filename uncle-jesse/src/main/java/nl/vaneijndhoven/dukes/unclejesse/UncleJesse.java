package nl.vaneijndhoven.dukes.unclejesse;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import nl.vaneijndhoven.dukes.hazardcounty.Characters;

public class UncleJesse extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        super.start();

        vertx.eventBus().sendObservable(Characters.DAISY.getCallsign() + "lane.start", new JsonObject().put("source", "file://Users/jpoint/Repositories/opencv-playground/src/main/resources/videos/full_run.mp4"));
    }
}
