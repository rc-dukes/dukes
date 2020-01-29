package org.rcdukes.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.rcdukes.common.Characters;
import org.rcdukes.common.ClusterStarter;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.DukesVerticle.Status;
import org.rcdukes.common.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		testVerticle.waitStatus(Status.started, ClusterStarter.MAX_START_TIME, 10);
		int minLoops=5;
		while (testVerticle.counter <=minLoops) {
		  Thread.sleep(TestVerticle.TEST_INTERVAL_MS);
		}
	}

}
