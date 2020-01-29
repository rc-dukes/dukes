package org.rcdukes.server;

import org.rcdukes.common.Characters;
import org.rcdukes.common.ClusterStarter;
import org.rcdukes.common.Config;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.Events;
import org.rcdukes.error.ErrorHandler;
import org.rcdukes.opencv.NativeLibrary;
import org.rcdukes.webcontrol.WebControl;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;

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
    // see https://stackoverflow.com/questions/59907813/how-to-filter-opencv-error-messages-in-java
    NativeLibrary.logStdErr();
    NativeLibrary.load();
    super.preStart();
    super.consumer(Events.REQUEST_CONFIG, this::requestConfig);
    super.consumer(this.character.getCallsign(), this::messageHandler);
    DukesVerticle[] verticles = { new WebControl() };
    starter.deployVerticles(verticles);
    for (DukesVerticle verticle : verticles) {
      verticle.waitStatus(Status.started,TIME_OUT, 10);
    }
    super.postStart();
  }
  
  private void requestConfig(Message<JsonObject> message) {
    try {
      JsonObject configJo=Config.getEnvironment().asJsonObject();
      super.send(Characters.BOSS_HOGG, configJo);
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }
  }
  
  private void messageHandler(Message<JsonObject> message) {
    JsonObject configJo = message.body();
    System.out.println(configJo);
    // , new DebugImageServer(),
    // new Action(), new Detector()
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
