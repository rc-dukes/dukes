package com.bitplan.rc.common;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import nl.vaneijndhoven.dukes.common.ClusterStarter;

/**
 * test the ClusterStarter
 * 
 * @author wf
 *
 */
public class TestClusterStarter {
	private static final Logger LOG = LoggerFactory.getLogger(TestClusterStarter.class);
	

	static class TestVerticle extends AbstractVerticle {
		int counter = 0;
		public static int TEST_INTERVAL_MS = 10;
		
		@Override
		public void start() {
			LOG.info("Starting TestVerticle");
			vertx.setPeriodic(TEST_INTERVAL_MS, id -> periodic());
		}

		public void periodic() {
			LOG.trace(String.format("periodic call %d", ++counter));
		}
	}

	@Test
	public void testClusterStarter() throws InterruptedException {
		ClusterStarter starter = new ClusterStarter();
		TestVerticle testVerticle = new TestVerticle();
		starter.deployVerticles(testVerticle);
		while (testVerticle.counter==0) {
			Thread.sleep(10);
		}
		Thread.sleep(TestVerticle.TEST_INTERVAL_MS*3);
		assertTrue(testVerticle.counter>=3);
	}

}
