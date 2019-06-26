package com.bitplan.rccar.server;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.EventBusOptions;
import nl.vaneijndhoven.dukes.common.Config;

/**
 * basic event bus tests see <a href=
 * 'https://github.com/eclipse-vertx/vert.x/blob/master/src/main/java/examples/EventBusExamples.java'>EventBusExamples.java</a>
 * 
 * @author wf
 *
 */
public class TestEventBus {
  @BeforeClass
  public static void configureLogging() {
    Config.configureLogging();
  }

  public void example0_5(Vertx vertx) {
    EventBus eb = vertx.eventBus();
    assertNotNull(eb);
  }

  @Test
  public void testExample14() throws InterruptedException {
    VertxOptions options = new VertxOptions()
        .setEventBusOptions(new EventBusOptions()
            .setClusterPublicHost("whatever").setClusterPublicPort(1234));
    boolean ready[]= {false};
    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        Vertx vertx = res.result();
        EventBus eventBus = vertx.eventBus();
        System.out.println("We now have a clustered event bus: " + eventBus);
        ready[0]=true;
      } else {
        System.out.println("Failed: " + res.cause());
      }
    });
    int msecs=0;
    // needs some 4-8 secs on a laptop
    // allow 10x as much for travis
    while (!ready[0] && msecs++<80000)
      Thread.sleep(1);
    System.out.println(String.format("%s startup after %d msecs",ready[0]?"finished":"timed out",msecs));
  }

}
