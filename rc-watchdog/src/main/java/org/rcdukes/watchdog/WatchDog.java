package org.rcdukes.watchdog;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;
import org.rcdukes.common.Characters;
import org.rcdukes.common.Config;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.Environment;
import org.rcdukes.drivecontrol.Car;
import rx.Subscription;

/**
 * WatchDog that will check if the vehicle is still reachable and send stop
 * command if not
 */
public class WatchDog extends DukesVerticle {

	protected static int HEARTBEAT_INTERVAL_MS = 150;
	protected static int MAX_MISSED_BEATS=6;
	private long lastHeartbeat = 0;
	private long heartBeatCount=0;
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

	/**
	 * receive heartbeat messages
	 * @param message
	 */
	private void heartbeat(Message<Object> message) {
		JsonObject messageBody = (JsonObject) message.body();
		if ("heartbeat".equals(messageBody.getString("type"))) {
			lastHeartbeat = System.currentTimeMillis();
		  this.heartBeatCount++;
			// LOG.trace("Heartbeat received, set last heartbeat to {}",
			// lastHeartbeat);
			if (!car.powerIsOn()) {
				LOG.info("First heartbeat received, power on");
				car.setPowerOn();
			}
		} else {
		  String msg=String.format("heartbeat message has wrong type:\n%s",messageBody.encodePrettily());
		  LOG.warn(msg);
		}
		if (heartBeatCount%12==0) {
		  LOG.info(String.format("heartbeat %d",this.heartBeatCount));
		}
	}

	private void checkHeartbeat() {
		long currentTime = System.currentTimeMillis();
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
