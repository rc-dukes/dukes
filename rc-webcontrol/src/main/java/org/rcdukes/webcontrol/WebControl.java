package org.rcdukes.webcontrol;

import org.rcdukes.common.Characters;
import org.rcdukes.common.Config;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.Environment;
import org.rcdukes.common.WebStarter;

import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.StaticHandler;

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
    WebStarter webStarter=new WebStarter(vertx,port);
    webStarter.mountEventBus(".*", ".*");
    // @TODO decide about sse support for configuration
    // router.route("/configsse/*")
    Router router = webStarter.getRouter();
    router.route()
        .handler(StaticHandler.create("web").setCachingEnabled(false));
    String media = Environment.dukesHome + "media";
    router.route("/media/*")
        .handler(StaticHandler.create().setAllowRootFileSystemAccess(true)
            .setWebRoot(media).setCachingEnabled(false)
            .setDirectoryListing(true));
    webStarter.startHttpServer();
    super.postStart();
  }

}
