package nl.vaneijndhoven.dukes.server;

import com.bitplan.error.ErrorHandler;

import io.vertx.core.json.JsonObject;
import nl.vaneijndhoven.detect.Detector;
import nl.vaneijndhoven.dukes.action.Action;
import nl.vaneijndhoven.dukes.common.Characters;
import nl.vaneijndhoven.dukes.common.ClusterStarter;
import nl.vaneijndhoven.dukes.common.Config;
import nl.vaneijndhoven.dukes.common.DukesVerticle;
import nl.vaneijndhoven.dukes.common.Events;
import nl.vaneijndhoven.dukes.imageview.DebugImageServer;
import nl.vaneijndhoven.dukes.webcontrol.WebControl;

/**
 * main entry point to start cluster
 *
 */
public class CarServer extends DukesVerticle {

  /**
   * construct me
   */
  public CarServer() {
    super(Characters.BOARS_NEST);
  }

  boolean debug = false;
  private ClusterStarter starter;

  @Override
  public void start() throws Exception {
    super.preStart();
    DukesVerticle[] verticles = { new WebControl(), new DebugImageServer(),
        new Action(), new Detector() };
    starter.deployVerticles(verticles);
    for (DukesVerticle verticle : verticles) {
      verticle.waitStatus(Status.started,20000, 10);
    }
    String cameraUrl = Config.getEnvironment().getString(Config.CAMERA_URL);
    vertx.eventBus().send(Events.STREAMADDED.name(),
        new JsonObject().put("source", cameraUrl));
    super.postStart();
  }

  /**
   * start the the cluster
   * 
   * @param args
   *          - command line arguments
   * @throws Exception
   *           on failure
   */
  public void mainInstance(String... args) {
    starter = new ClusterStarter();
    starter.prepare();
    // bootstrap the deployment by deploying me
    try {
      starter.deployVerticles(this);
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }
  }

  /**
   * static main routine
   * 
   * @param args
   */
  public static void main(String... args) {
    CarServer carServer = new CarServer();
    carServer.mainInstance(args);
  }
}
