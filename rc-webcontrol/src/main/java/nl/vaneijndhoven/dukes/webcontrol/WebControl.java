package nl.vaneijndhoven.dukes.webcontrol;

import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;
import nl.vaneijndhoven.dukes.common.Characters;
import nl.vaneijndhoven.dukes.common.Config;
import nl.vaneijndhoven.dukes.common.DukesVerticle;

/**
 * Manual UI web based control (Boss Hogg)
 *
 */
public class WebControl extends DukesVerticle {

  public WebControl() {
    super(Characters.BOSS_HOGG);
  }
  
  @Override
  public void start() throws Exception {
    super.preStart();
    int port=Config.getEnvironment().getInteger(Config.WEBCONTROL_PORT);
    LOG.info("using port "+port);
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
    super.postStart();
  }

}
