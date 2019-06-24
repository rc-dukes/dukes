package com.bitplan.watchdog;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

/**
 * test the WatchDog Verticle
 * @author wf
 *
 */
@RunWith(VertxUnitRunner.class)
public class TestWatchDog {

  @Test
  public void testWatchDog() {
    
  }
  private Vertx vertx;

  final AtomicBoolean loaded = new AtomicBoolean(false);

  /**
   * @see <a href="https://stackoverflow.com/q/32435571/1497139">Stackoverflow Question for Vertx Unit Testing</a>
   * @param context
   * @throws IOException
   */
  @Before
  public void setUp(TestContext context) throws IOException {
   ClusterManager mgr = new HazelcastClusterManager();
   VertxOptions options = new VertxOptions().setClusterManager(mgr);
   Vertx.clusteredVertx(options, new Handler<AsyncResult<Vertx>>() {
       @Override
       public void handle(AsyncResult<Vertx> res) {
           if (res.succeeded()) {
               vertx = res.result();

               DeploymentOptions options = new DeploymentOptions()
                       .setConfig(new JsonObject().put("http.port", 8080)
                       );
               //vertx.deployVerticle(MyWebService.class.getName(), options, context.asyncAssertSuccess());
               System.out.println("SUCCESS");
               loaded.set(true);
           } else {
               System.out.println("FAILED");
           }
        }
      });
  }

  @Test
  public void printSomething(TestContext context) {
   Async async = context.async(); // wait for context
   System.out.println("Print from method printSomething()");
   async.complete();
  }

}
