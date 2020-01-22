package org.rcsdukes.server;

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
  public class Server extends AbstractVerticle {
    static final  String ADDRESS="echo";
    public void start(Future<Void> fut) {

      TcpEventBusBridge bridge = TcpEventBusBridge.create(vertx,
          new BridgeOptions()
              .addInboundPermitted(new PermittedOptions().setAddress(ADDRESS))
              .addOutboundPermitted(new PermittedOptions().setAddress(ADDRESS)));

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

    }
  }

}
