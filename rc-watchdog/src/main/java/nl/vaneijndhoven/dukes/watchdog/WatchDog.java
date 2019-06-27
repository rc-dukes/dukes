package nl.vaneijndhoven.dukes.watchdog;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import nl.vaneijndhoven.dukes.car.Command;
import nl.vaneijndhoven.dukes.common.Characters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscription;

/**
 * WatchDog that will check if the vehicle is still reachable and send stop
 * command if not
 */
public class WatchDog extends AbstractVerticle {

  public static final int HEARTBEAT_INTERVAL_MS = 150;
  private static final Logger LOG = LoggerFactory.getLogger(WatchDog.class);
  private long lastHeartbeat = 0;

  @Override
  public void start() {
    LOG.info("Starting WatchDog (Flash - heartbeat guard dog)");
    // Command.statusLedOff();

    vertx.setPeriodic(HEARTBEAT_INTERVAL_MS, id -> checkHeartbeat());
    Subscription subscription = vertx.eventBus()
        .consumer(Characters.FLASH.getCallsign()).toObservable()
        .subscribe(this::heartbeat);

    LOG.info("WatchDog Flash started ");
  }

  private void heartbeat(Message<Object> message) {
    JsonObject messageBody = (JsonObject) message.body();
    if ("heartbeat".equals(messageBody.getString("type"))) {
      lastHeartbeat = System.currentTimeMillis();
      // LOG.trace("Heartbeat received, set last heartbeat to {}",
      // lastHeartbeat);
      if (!Command.powerIsOn()) {
        LOG.info("First heartbeat received, power on");
        Command.setPowerOn();
      }
    }
  }

  private void checkHeartbeat() {
    long currentTime = System.currentTimeMillis();
    // LOG.trace("Check heartbeat, current time: {}", currentTime);
    if (currentTime - lastHeartbeat > (6 * HEARTBEAT_INTERVAL_MS)) {
      if (Command.powerIsOn()) {
        // missed 2 heartbeats
        LOG.trace("Missed at least 2 heartbeats.");
        LOG.error("Client connection lost, stopping car and turning off led");
        LOG.info("Heartbeat off -> power off");
        vertx.eventBus().send(Characters.BO.getCallsign(),
            new JsonObject().put("type", "motor").put("speed", "stop"));

        // failsafe: send stop command again after 200ms
        vertx.setTimer(200, fired -> sendStopCommand());

        Command.setPowerOff();
      }
    }
  }

  private void sendStopCommand() {
    vertx.eventBus().send(Characters.BO.getCallsign(),
        new JsonObject().put("type", "motor").put("speed", "stop"));
  }

}
