package org.rcsdukes.server;

import org.junit.Test;
import org.rcdukes.common.Characters;
import org.rcdukes.common.ClusterStarter;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.Environment;
import org.rcdukes.common.DukesVerticle.Status;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import io.vertx.rxjava.ext.eventbus.bridge.tcp.TcpEventBusBridge;

public class TestEventBusBridge {

  /**
   *
   * @author jay
   */
  public class EchoServer extends DukesVerticle {

    public EchoServer(Characters character) {
      super(character);
    }

    static final String ADDRESS = "echo";

    @Override
    public void start() throws Exception {
      super.preStart();

      TcpEventBusBridge bridge = TcpEventBusBridge.create(vertx,
          new BridgeOptions()
              .addInboundPermitted(new PermittedOptions().setAddress(ADDRESS))
              .addOutboundPermitted(
                  new PermittedOptions().setAddress(ADDRESS)));

      bridge.listen(7001, res -> {
        if (res.succeeded()) {

        } else {
          System.exit(0);
        }
      });
      EventBus eb = vertx.eventBus();

      MessageConsumer<JsonObject> consumer = eb.consumer(ADDRESS, message -> {
        message.reply(message.body());
      });
      super.postStart();
    }
  }

  @Test
  public void testEventBusBridge() throws Exception {
    Environment.mock();
    ClusterStarter starter = new ClusterStarter();
    starter.prepare();
    EchoServer echoVerticle = new EchoServer(Characters.COY);
    DukesVerticle.debug = true;
    starter.deployVerticles(echoVerticle);
    echoVerticle.waitStatus(Status.started, 15000, 10);
    starter.undeployVerticle(echoVerticle);
    if (!TestSuite.isTravis())
      echoVerticle.waitStatus(Status.stopped, 15000, 10);
  }

}
