package org.rcdukes.server;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.rcdukes.action.Action;
import org.rcdukes.common.Characters;
import org.rcdukes.common.ClusterStarter;
import org.rcdukes.common.Config;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.Events;
import org.rcdukes.detect.Detector;
import org.rcdukes.error.ErrorHandler;
import org.rcdukes.imageview.DebugImageServer;
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

  private ClusterStarter starter;
  int TIME_OUT=20000;
  boolean verticlesStarted=false;

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
      if (!verticlesStarted) {
        starter.deployVerticles(new DebugImageServer(),new Action(), new Detector());
        verticlesStarted=true;
      }
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }
  }
  
  private void messageHandler(Message<JsonObject> message) {
    JsonObject configJo = message.body();
    System.out.println(configJo);
    // , 
  }
  
  protected CmdLineParser parser;
  @Option(name = "-d", aliases = {
      "--debug" }, usage = "debug\ncreate additional debug output if this switch is used")
  protected boolean debug = false;

  @Option(name = "-ch", aliases = { "--hostname" }, usage = "clusterHostname")
  String clusterHostname = null;
  @Option(name = "-ph", aliases = { "--publichost" }, usage = "public hostname")
  String publicHost = null;
  
  /**
   * parse the given Arguments
   * 
   * @param args
   * @throws CmdLineException
   */
  public void parseArguments(String[] args) throws CmdLineException {
    parser = new CmdLineParser(this);
    parser.parseArgument(args);
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
    try {
      this.parseArguments(args);
    } catch (CmdLineException cle) {
      ErrorHandler.getInstance().handle(cle);
      parser.printUsage(System.err);
      System.exit(1);
    }
    starter = new ClusterStarter();
    starter.prepare();

    starter.configureCluster(clusterHostname, publicHost);
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
