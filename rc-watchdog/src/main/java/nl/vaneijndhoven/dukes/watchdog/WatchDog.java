package nl.vaneijndhoven.dukes.watchdog;

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
	  super(Characters.FLASH);
		this.car = car;
		Environment env = Config.getEnvironment();
		HEARTBEAT_INTERVAL_MS=env.getInteger(Config.WATCHDOG_HEARTBEAT_INTERVAL_MS);
		MAX_MISSED_BEATS=env.getInteger(Config.WATCHDOG_MAX_MISSED_BEATS);
	}

	@Override
	public void start() {
	  super.preStart();
		// Command.statusLedOff();

		vertx.setPeriodic(HEARTBEAT_INTERVAL_MS, id -> checkHeartbeat());
		subscription = vertx.eventBus().consumer(Characters.FLASH.getCallsign()).toObservable()
				.subscribe(this::heartbeat);

		super.postStart();
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
	  send(Characters.BO,"type", "motor","speed", "stop");
	}

}
