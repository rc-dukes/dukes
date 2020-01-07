package com.bitplan.rc.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vaneijndhoven.dukes.common.Characters;
import nl.vaneijndhoven.dukes.common.ClusterStarter;
import nl.vaneijndhoven.dukes.common.DukesVerticle;
import nl.vaneijndhoven.dukes.common.Environment;

/**
 * test the ClusterStarter
 * 
 * @author wf
 *
 */
public class TestClusterStarter {
	private static final Logger LOG = LoggerFactory.getLogger(TestClusterStarter.class);

	public static boolean debug = false;

	static class TestVerticle extends DukesVerticle {
		public TestVerticle() {
      super(Characters.BO);
    }

    int counter = 0;
		public static int TEST_INTERVAL_MS = 20;

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
	public void testClusterStarter() throws Exception {
		Environment.mock();
		ClusterStarter starter = new ClusterStarter();
		TestVerticle testVerticle = new TestVerticle();
		starter.deployVerticles(testVerticle);
		int minLoops=5;
		while (testVerticle.counter <=minLoops) {
		  Thread.sleep(TestVerticle.TEST_INTERVAL_MS);
		}
		String cameraurl=testVerticle.config().getString("camera.url");
		assertEquals("http://pi.doe.com/html/cam_pic_new.php",cameraurl);
		if (debug) {
			for (String key : testVerticle.config().fieldNames()) {
				LOG.info(key);
			}
		}
	}

}
