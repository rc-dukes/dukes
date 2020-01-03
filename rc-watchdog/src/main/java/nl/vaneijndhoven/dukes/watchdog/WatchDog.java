package nl.vaneijndhoven.dukes.watchdog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;
import nl.vaneijndhoven.dukes.common.Characters;
import nl.vaneijndhoven.dukes.common.Config;
import nl.vaneijndhoven.dukes.common.DukesVerticle;
import nl.vaneijndhoven.dukes.common.Environment;
import nl.vaneijndhoven.dukes.drivecontrol.Car;
import rx.Subscription;

/**
 * WatchDog that will check if the vehicle is still reachable and send stop
 * command if not
 */
public class WatchDog extends DukesVerticle {

	protected static int HEARTBEAT_INTERVAL_MS = 150;
	protected static int MAX_MISSED_BEATS=6;
	private static final Logger LOG = LoggerFactory.getLogger(WatchDog.class);
	private long lastHeartbeat = 0;
	private Car car;
	protected Subscription subscription;
	

	/**
	 * create watch dog for the given car
	 * 
	 * @param car
	 * @throws Exception 
	 */
	public WatchDog(Car car) throws Exception {
		this.car = car;
		Environment env = Config.getEnvironment();
		HEARTBEAT_INTERVAL_MS=env.getInteger(Config.WATCHDOG_HEARTBEAT_INTERVAL_MS);
		MAX_MISSED_BEATS=env.getInteger(Config.WATCHDOG_MAX_MISSED_BEATS);
	}

	@Override
	public void start() {
		LOG.info("Starting WatchDog (Flash - heartbeat guard dog)");
		// Command.statusLedOff();

		vertx.setPeriodic(HEARTBEAT_INTERVAL_MS, id -> checkHeartbeat());
		subscription = vertx.eventBus().consumer(Characters.FLASH.getCallsign()).toObservable()
				.subscribe(this::heartbeat);

		LOG.info("WatchDog Flash started ");
		started=true;
	}

	private void heartbeat(Message<Object> message) {
		JsonObject messageBody = (JsonObject) message.body();
		if ("heartbeat".equals(messageBody.getString("type"))) {
			lastHeartbeat = System.currentTimeMillis();
			// LOG.trace("Heartbeat received, set last heartbeat to {}",
			// lastHeartbeat);
			if (!car.powerIsOn()) {
				LOG.info("First heartbeat received, power on");
				car.setPowerOn();
			}
		}
	}

	private void checkHeartbeat() {
		long currentTime = System.currentTimeMillis();
		// LOG.trace("Check heartbeat, current time: {}", currentTime);
		if (currentTime - lastHeartbeat > (MAX_MISSED_BEATS * HEARTBEAT_INTERVAL_MS)) {
			if (car.powerIsOn()) {
				// missed maximum number of allowed heartbeats
				LOG.trace("Missed at least "+MAX_MISSED_BEATS+" heartbeats.");
				LOG.error("Client connection lost, stopping car and turning off led");
				LOG.info("Heartbeat off -> power off");
				sendStopCommand();
				
				// failsafe: send stop command again after 200ms
				vertx.setTimer(200, fired -> sendStopCommand());

				car.setPowerOff();
			}
		}
	}

	private void sendStopCommand() {
		vertx.eventBus().send(Characters.BO.getCallsign(), new JsonObject().put("type", "motor").put("speed", "stop"));
	}

}
