package org.rcdukes.webcontrol;

import org.rcdukes.common.Characters;
import org.rcdukes.common.Config;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.Environment;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;

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
    int port = Config.getEnvironment().getInteger(Config.WEBCONTROL_PORT);
    LOG.info("using port " + port);
    Router router = Router.router(vertx);
    BridgeOptions options = new BridgeOptions();
    options.addOutboundPermitted(new PermittedOptions().setAddressRegex(".*"));
    options.addInboundPermitted(new PermittedOptions().setAddressRegex(".*"));
    // upto 3.7.1
    // router.route("/eventbus/*")
    // .handler(SockJSHandler.create(vertx).bridge(options));
    // 3.8.2 up
    // https://github.com/vert-x3/wiki/wiki/3.8.2-Deprecations-and-breaking-changes#sockjshandler-changes
    // https://stackoverflow.com/questions/58940327/vert-x-sockjshandler-class
    SockJSHandler webSockHandler = SockJSHandler.create(vertx);
    Router webSockRouter = webSockHandler.bridge(options);
    router.mountSubRouter("/eventbus",webSockRouter);
    // @TODO decide about sse support for configuration
    // router.route("/configsse/*")
    router.route()
        .handler(StaticHandler.create("web").setCachingEnabled(false));
    String media = Environment.dukesHome + "media";
    router.route("/media/*")
        .handler(StaticHandler.create().setAllowRootFileSystemAccess(true)
            .setWebRoot(media).setCachingEnabled(false)
            .setDirectoryListing(true));
    HttpServerOptions httpServerOptions = new HttpServerOptions();
    httpServerOptions.setMaxWebsocketFrameSize(1024*1024);
    HttpServer server = vertx.createHttpServer(httpServerOptions); 
    server.requestHandler(router).listen(port);

    // vertx.eventBus().sendObservable(Characters.DAISY.getCallsign() +
    // "lane.start", new JsonObject().put("source",
    // "file://Users/jpoint/Repositories/opencv-playground/src/main/resources/videos/full_run.mp4"));
    super.postStart();
  }

}
