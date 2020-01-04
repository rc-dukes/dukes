package nl.vaneijndhoven.dukes.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bitplan.error.ErrorHandler;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import nl.vaneijndhoven.detect.Detector;
import nl.vaneijndhoven.dukes.action.Action;
import nl.vaneijndhoven.dukes.common.ClusterStarter;
import nl.vaneijndhoven.dukes.common.Config;
import nl.vaneijndhoven.dukes.common.Events;
import nl.vaneijndhoven.dukes.imageview.ImageView;
import nl.vaneijndhoven.dukes.webcontrol.WebControl;

/**
 * main entry point to start cluster
 *
 */
public class CarServer {

  private static final Logger LOG = LoggerFactory.getLogger(CarServer.class);

  /**
   * start the the cluster
   * 
   * @param args
   *          - command line arguments
   * @throws Exception
   *           on failure
   */
  public static void main(String... args) {
    Throwable error[] = { null };
    try {
      ClusterStarter starter = new ClusterStarter();
      starter.prepare();

      String cameraUrl = Config.getEnvironment().getString(Config.CAMERA_URL);

      LOG.info("Firing up Car Server Boars Nest (UI runner) using cameraUrl "
          + cameraUrl);
      // TODO check necessity of this workaround
      // setting check interval to 1 h
      VertxOptions options = starter.getOptions()
          .setBlockedThreadCheckInterval(1000 * 60 * 60);

      Vertx.clusteredVertx(options, resultHandler -> {
        Vertx vertx = resultHandler.result();
        DeploymentOptions deploymentOptions;
        try {
          deploymentOptions = starter.getDeployMentOptions(true);
          // deprecated
          // see
          // https://vertx.io/docs/apidocs/io/vertx/core/DeploymentOptions.html#setMultiThreaded-boolean-
          // deploymentOptions.setMultiThreaded(true);
          vertx.deployVerticle(new WebControl());
          vertx.deployVerticle(new ImageView());

          boolean enableAutoPilot = true;

          if (enableAutoPilot) {
            vertx.deployVerticle(new Action());
            vertx.deployVerticle(new Detector(), deploymentOptions, async -> {
              vertx.eventBus().send(Events.STREAMADDED.name(),
                  new JsonObject().put("source", cameraUrl));
            });
          }
        } catch (Exception e) {
          error[0] = e;
        }

      });
    } catch (Throwable th) {
      error[0] = th;
    }
    if (error[0] != null)
      ErrorHandler.getInstance().handle(error[0]);
  }

}
