package org.rcdukes.webcontrol;

import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;
import org.rcdukes.common.Characters;
import org.rcdukes.common.Config;
import org.rcdukes.common.DukesVerticle;

/**
 * Manual UI web based control (Boss Hogg)
 *
 */
public class WebControl extends DukesVerticle {

  /**
   * construct me
   */
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
    options.addOutboundPermitted(new PermittedOptions().setAddressRegex(".*"));
    options.addInboundPermitted(new PermittedOptions().setAddressRegex(".*"));
    router.route("/eventbus/*")
        .handler(SockJSHandler.create(vertx).bridge(options));
    // @TODO decide about sse support for configuration
    // router.route("/configsse/*")
    router.route()
        .handler(StaticHandler.create("web").setCachingEnabled(false));
    vertx.createHttpServer().requestHandler(router).listen(port);

    // vertx.eventBus().sendObservable(Characters.DAISY.getCallsign() +
    // "lane.start", new JsonObject().put("source",
    // "file://Users/jpoint/Repositories/opencv-playground/src/main/resources/videos/full_run.mp4"));
    super.postStart();
  }

}
