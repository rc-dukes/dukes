package com.bitplan.watchdog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import nl.vaneijndhoven.dukes.common.Characters;
import nl.vaneijndhoven.dukes.common.ClusterStarter;
import nl.vaneijndhoven.dukes.common.Config;
import nl.vaneijndhoven.dukes.common.Environment;
import nl.vaneijndhoven.dukes.common.DukesVerticle.Status;
import nl.vaneijndhoven.dukes.drivecontrol.Car;
import nl.vaneijndhoven.dukes.drivecontrol.TestCar;
import nl.vaneijndhoven.dukes.watchdog.WatchDog;

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
