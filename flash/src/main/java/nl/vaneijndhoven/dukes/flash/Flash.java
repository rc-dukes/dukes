package nl.vaneijndhoven.dukes.flash;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import nl.vaneijndhoven.dukes.car.Command;
import nl.vaneijndhoven.dukes.hazardcounty.Characters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscription;


public class Flash extends AbstractVerticle {

    public static final int HEARTBEAT_INTERVAL_MS = 150;
    private static final Logger LOG = LoggerFactory.getLogger(Flash.class);
    private long lastHeartbeat = 0;

    @Override
    public void start() {
        LOG.info("Starting Flash (heartbeat guard dog)");
//        Command.statusLedOff();

        vertx.setPeriodic(HEARTBEAT_INTERVAL_MS, id -> checkHeartbeat());
        Subscription subscription = vertx.eventBus().consumer(Characters.FLASH.getCallsign()).toObservable()
                .subscribe(this::heartbeat);

        LOG.info("Flash started");
    }

    private void heartbeat(Message<Object> message) {
        JsonObject messageBody = (JsonObject)message.body();
        if ("heartbeat".equals(messageBody.getString("type"))) {
            lastHeartbeat = System.currentTimeMillis();
            LOG.trace("Heartbeat received, set last heartbeat to {}", lastHeartbeat);
            if (!Command.powerIsOn()) {
                LOG.info("First heartbeat received, power on");
                Command.setPowerOn();
            }
        }
    }

    private void checkHeartbeat() {
        long currentTime = System.currentTimeMillis();
        LOG.trace("Check heartbeat, current time: {}", currentTime);
        if (currentTime - lastHeartbeat > (3 * HEARTBEAT_INTERVAL_MS)) {
            // missed 2 heartbeats
            LOG.trace("Missed at least 2 heartbeats.");
            if (Command.powerIsOn()) {
                LOG.error("Client connection lost, stopping car and turning off led");
                vertx.eventBus().send(Characters.BO.getCallsign(), new JsonObject().put("type", "speedDirect").put("speed", 0));
                Command.setPowerOff();
            }
        }
    }

}
