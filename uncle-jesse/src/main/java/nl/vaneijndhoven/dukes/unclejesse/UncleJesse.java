package nl.vaneijndhoven.dukes.unclejesse;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import nl.vaneijndhoven.dukes.hazardcounty.Characters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UncleJesse extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(UncleJesse.class);

    @Override
    public void start() throws Exception {
        LOG.info("Starting Uncle Jesse (UI)");
        super.start();

        vertx.eventBus().sendObservable(Characters.DAISY.getCallsign() + "lane.start", new JsonObject().put("source", "file://Users/jpoint/Repositories/opencv-playground/src/main/resources/videos/full_run.mp4"));

        LOG.info("Uncle Jesse started");
    }
}
