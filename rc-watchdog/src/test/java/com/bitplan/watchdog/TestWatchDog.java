package com.bitplan.watchdog;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.tinkerpop.shaded.minlog.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import nl.vaneijndhoven.dukes.common.Characters;
import nl.vaneijndhoven.dukes.common.ClusterStarter;
import nl.vaneijndhoven.dukes.common.Events;
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
		Car car=TestCar.getCar();
		WatchDog watchDog=new WatchDog(car);
		clusterStarter.deployVerticles(watchDog);
		while (!watchDog.isStarted()) {
			Thread.sleep(10);
		}
		Vertx vertx=clusterStarter.getVertx();
		assertNotNull(vertx);
		vertx.eventBus().send(Characters.FLASH.getCallsign(),
	              new JsonObject().put("type", "heartbeat"));
		// car should power on by heartbeat
		int loops=0;
		while(!car.powerIsOn() && loops<=100) {
			Thread.sleep(10);
			loops++;
		}
		assertTrue(loops<100);
		LOG.info(String.format("car powered on after %3d msecs",loops*10));
	}

}
