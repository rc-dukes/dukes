package com.bitplan.watchdog;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.tinkerpop.shaded.minlog.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bitplan.rc.common.TestClusterStarter;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import nl.vaneijndhoven.dukes.common.ClusterStarter;

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
	public void testWatchDog() {

	}

	private Vertx vertx;

	final AtomicBoolean loaded = new AtomicBoolean(false);

	/**
	 * @see <a href="https://stackoverflow.com/q/32435571/1497139">Stackoverflow
	 *      Question for Vertx Unit Testing</a>
	 * @param context
	 * @throws IOException
	 */
	@Before
	public void setUp(TestContext context) throws IOException {
		ClusterStarter starter=new ClusterStarter();
		starter.clusteredVertx(new Handler<AsyncResult<Vertx>>() {
			@Override
			public void handle(AsyncResult<Vertx> res) {
				if (res.succeeded()) {
					vertx = res.result();

					DeploymentOptions options = new DeploymentOptions()
							.setConfig(new JsonObject().put("http.port", 8080));
					// vertx.deployVerticle(MyWebService.class.getName(), options,
					// context.asyncAssertSuccess());
					Log.info("async deployment SUCCESS");
					loaded.set(true);
				} else {
					Log.info("async deployment FAILED");
				}
			}
		});
	}

	@Test
	public void testAsync(TestContext context) {
		Async async = context.async(); // wait for context
		assertNotNull(async);
		// System.out.println("Print from method printSomething()");
		async.complete();
	}

}
