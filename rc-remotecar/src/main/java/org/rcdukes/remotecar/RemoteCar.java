package org.rcdukes.remotecar;

import org.rcdukes.car.CarVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.rcdukes.error.ErrorHandler;

import io.vertx.core.VertxOptions;
import org.rcdukes.common.ClusterStarter;
import org.rcdukes.common.Config;
import org.rcdukes.common.Environment;
import org.rcdukes.drivecontrol.Car;
import org.rcdukes.watchdog.WatchDog;

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
			  String clusterHost=Config.getEnvironment().getString(Config.REMOTECAR_HOST);
			  
				LOG.info(String.format("Running on the Raspberry Pi, activating Vert.x clustering over the network with clusterHost %s.",clusterHost));
				// activate clustering over the network on the right interface.
				options.setClusterHost(clusterHost);
			} else {
				LOG.info("Not running on the Raspberry Pi, not activating Vert.x clustering over the network.");
			}
			starter.deployVerticles(new WatchDog(car), new CarVerticle());
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
