package org.rcdukes.server;

import org.rcdukes.action.Action;
import org.rcdukes.common.Characters;
import org.rcdukes.common.ClusterStarter;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.detect.Detector;
import org.rcdukes.error.ErrorHandler;
import org.rcdukes.imageview.DebugImageServer;
import org.rcdukes.opencv.NativeLibrary;
import org.rcdukes.webcontrol.WebControl;

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
  int TIME_OUT=20000;

  @Override
  public void start() throws Exception {
    NativeLibrary.load();
    super.preStart();
    DukesVerticle[] verticles = { new WebControl(), new DebugImageServer(),
        new Action(), new Detector() };
    starter.deployVerticles(verticles);
    for (DukesVerticle verticle : verticles) {
      verticle.waitStatus(Status.started,TIME_OUT, 10);
    }
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
