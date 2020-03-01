package org.rcdukes.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;

/**
 * Webserver and eventbus starter
 * @author wf
 *
 */
public class WebStarter {
  protected static final Logger LOG = LoggerFactory
      .getLogger(WebStarter.class);

  private int port;
  private Vertx vertx;

  private Router router;
  /**
   * @return the router
   */
  public Router getRouter() {
    return router;
  }

  /**
   * @param router the router to set
   */
  public void setRouter(Router router) {
    this.router = router;
  }

  /**
   * create a webstarter
   * @param vertx
   * @param port
   */
  public WebStarter(Vertx vertx, int port) {
    LOG.info("creating WebStarter using port " + port);
    this.vertx=vertx;
    this.port=port;
    router = Router.router(vertx);
  }
  
  /**
   * mount the eventbus with the given regular expressions
   * @param inRegex
   * @param outRegex
   */
  public void mountEventBus(String inRegex,String outRegex) {
    LOG.info("mounting eventbus");
    BridgeOptions options = new BridgeOptions();
    options.addOutboundPermitted(new PermittedOptions().setAddressRegex(outRegex));
    options.addInboundPermitted(new PermittedOptions().setAddressRegex(inRegex));
    // upto 3.7.1
    // router.route("/eventbus/*")
    // .handler(SockJSHandler.create(vertx).bridge(options));
    // 3.8.2 up
    // https://github.com/vert-x3/wiki/wiki/3.8.2-Deprecations-and-breaking-changes#sockjshandler-changes
    // https://stackoverflow.com/questions/58940327/vert-x-sockjshandler-class
    SockJSHandler webSockHandler = SockJSHandler.create(vertx);
    Router webSockRouter = webSockHandler.bridge(options);
    getRouter().mountSubRouter("/eventbus",webSockRouter);
  }

  /**
   * start the http server
   */
  public void startHttpServer() {
    HttpServerOptions httpServerOptions = new HttpServerOptions();
    httpServerOptions.setMaxWebsocketFrameSize(1024*1024);
    HttpServer server = vertx.createHttpServer(httpServerOptions); 
    server.requestHandler(router).listen(port);
  }

  
}
