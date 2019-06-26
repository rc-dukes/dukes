package nl.vaneijndhoven.dukes.webcontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;
import nl.vaneijndhoven.dukes.common.Environment;

/**
 * Manual UI web based control (Boss Hogg)
 *
 */
public class WebControl extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(WebControl.class);

  @Override
  public void start() throws Exception {
    int port=Environment.getInstance().getInteger("carserver.port");
    LOG.info("Starting WebControl Boss Hogg (manual UI controller) on port "+port);

    super.start();

    Router router = Router.router(vertx);
    BridgeOptions options = new BridgeOptions();

    // Stream.of(Characters.values()).forEach(character -> {
    // PermittedOptions permitted = new
    // PermittedOptions().setAddress(character.getCallsign());
    // options.addOutboundPermitted(permitted);
    // options.addInboundPermitted(permitted);
    //
    // });
    // Stream.of(Events.values()).forEach(event -> {
    // options.addOutboundPermitted(new
    // PermittedOptions().setAddress(event.name()))
    // });

    options.addOutboundPermitted(new PermittedOptions().setAddressRegex(".*"));
    options.addInboundPermitted(new PermittedOptions().setAddressRegex(".*"));
    router.route("/eventbus/*")
        .handler(SockJSHandler.create(vertx).bridge(options));
    router.route()
        .handler(StaticHandler.create("web").setCachingEnabled(false));
    vertx.createHttpServer().requestHandler(router::accept).listen(port);

    // vertx.eventBus().sendObservable(Characters.DAISY.getCallsign() +
    // "lane.start", new JsonObject().put("source",
    // "file://Users/jpoint/Repositories/opencv-playground/src/main/resources/videos/full_run.mp4"));

    LOG.info("WebControl Boss Hogg started");
  }

}
