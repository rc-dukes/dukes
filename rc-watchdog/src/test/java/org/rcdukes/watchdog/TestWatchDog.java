package org.rcdukes.watchdog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rcdukes.watchdog.WatchDog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.rcdukes.common.Characters;
import org.rcdukes.common.ClusterStarter;
import org.rcdukes.common.Config;
import org.rcdukes.common.Environment;
import org.rcdukes.common.DukesVerticle.Status;
import org.rcdukes.drivecontrol.Car;
import org.rcdukes.drivecontrol.TestCar;

/**
 * test the WatchDog Verticle
 * 
 * @author wf
 *
 */
@RunWith(VertxUnitRunner.class)
public class TestWatchDog {
	private static final Logger LOG = LoggerFactory.getLogger(TestWatchDog.class);
	@Test
	public void testWatchDog() throws Exception {
		ClusterStarter clusterStarter=new ClusterStarter();
		// sideffect is to use dummy configuration
		// which also effects watchdog
		Car car=TestCar.getCar();
		Environment env = Config.getEnvironment();
		int heartBeatInterval=env.getInteger(Config.WATCHDOG_HEARTBEAT_INTERVAL_MS);
		assertEquals(20,heartBeatInterval);
		int maxMissedBeats=env.getInteger(Config.WATCHDOG_MAX_MISSED_BEATS);
		assertEquals(2,maxMissedBeats);

		WatchDog watchDog=new WatchDog(car);
		
		clusterStarter.deployVerticles(watchDog);
		watchDog.waitStatus(Status.started,20000,10);
		Vertx vertx=clusterStarter.getVertx();
		assertNotNull(vertx);
		assertEquals(watchDog.getVertx(),vertx);
		watchDog.send(Characters.FLASH,"type","heartbeat");
		// car should power on by heartbeat
		int loops=0;
		while(!car.powerIsOn() && loops<=100) {
			Thread.sleep(10);
			loops++;
		}
		assertTrue(loops<100);
		LOG.info(String.format("car powered on after %3d msecs",loops*10));
		Thread.sleep(heartBeatInterval*(maxMissedBeats+3));
		assertTrue(!car.powerIsOn());
	}

}
