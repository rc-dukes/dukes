package nl.vaneijndhoven.dukes.remotecar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bitplan.error.ErrorHandler;

import io.vertx.core.VertxOptions;
import nl.vaneijndhoven.dukes.car.CarVerticle;
import nl.vaneijndhoven.dukes.common.ClusterStarter;
import nl.vaneijndhoven.dukes.common.Config;
import nl.vaneijndhoven.dukes.common.Environment;
import nl.vaneijndhoven.dukes.drivecontrol.Car;
import nl.vaneijndhoven.dukes.watchdog.WatchDog;

/**
 * Runner to start the remote cluster on the car
 *
 */
public class RemoteCar {

	private static final Logger LOG = LoggerFactory.getLogger(RemoteCar.class);

	/**
	 * start me with given command line parameters
	 * 
	 * @param args
	 *            - the command line parameters to use
	 * @throws Exception
	 */
	public static void main(String... args) {
		ClusterStarter starter = new ClusterStarter();
		starter.prepare();
		Car car;
		try {
			car = Car.getInstance(); // the one and only to be used also by the verticle!
			configureShutdownHook(car);

			// Set wheel and speed to neutral.
			car.stop();
			// Command.stop();

			LOG.info("Firing up General Lee vert.x and core controller, tutu tu tu tu tutututu tututu...");

			VertxOptions options = starter.getOptions();

			if (Environment.getInstance().runningOnRaspberryPi()) {
				LOG.info("Running on the Raspberry Pi, activating Vert.x clustering over the network.");
				// activate clustering over the network on the right interface.
				options.setClusterHost(Config.getEnvironment().getString(Config.REMOTECAR_HOST));
			} else {
				LOG.info("Not running on the Raspberry Pi, not activating Vert.x clustering over the network.");
			}
			starter.deployVerticles(new WatchDog(car), new CarVerticle());
			/*
			 * vertx.deployVerticle(new Daisy(), deploymentOptions);
			 * vertx.deployVerticle(new Daisy(), deploymentOptions, async -> {
			 * 
			 * if (async.failed()) { return; }
			 * 
			 * // use example video as source // String userpath="@TODO set here" //
			 * vertx.eventBus().send(Events.STREAMADDED.name(), new
			 * JsonObject().put("source",
			 * "file://"+userpath+"dukes/daisy/src/main/resources/videos/full_run.mp4" ));
			 * }); vertx.deployVerticle(new Luke(), async -> { if (async.failed()) { return;
			 * }
			 * 
			 * // vertx.eventBus().send(Characters.LUKE.getCallsign() + ":" +
			 * Luke.START_DRAG_NAVIGATION,null); });
			 */
			// vertx.deployVerticle(new UncleJesse());
		} catch (Throwable th) {
			ErrorHandler.getInstance().handle(th);
		}
	}

	private static void configureShutdownHook(Car car) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				LOG.info("Activating shutdown hook.");
				car.stop();
			}
		});
	}

}
