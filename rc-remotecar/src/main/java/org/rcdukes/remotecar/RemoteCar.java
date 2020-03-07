package org.rcdukes.remotecar;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.rcdukes.car.CarVerticle;
import org.rcdukes.common.Characters;
import org.rcdukes.common.ClusterStarter;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.Environment;
import org.rcdukes.common.Events;
import org.rcdukes.drivecontrol.Car;
import org.rcdukes.error.ErrorHandler;
import org.rcdukes.watchdog.WatchDog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.disposables.Disposable;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;

/**
 * Runner to start the remote cluster on the car
 *
 */
public class RemoteCar extends DukesVerticle {

  private ClusterStarter starter;
  private Car car;
  private Disposable startRequester;
  private WatchDog watchDog;
  private CarVerticle carVerticle;

  /**
   * construct me as a verticle
   */
  public RemoteCar() {
    super(Characters.GENERAL_LEE);
  }

  private static final Logger LOG = LoggerFactory.getLogger(RemoteCar.class);

  @Override
  public void start() throws Exception {
    super.preStart();
    super.consumer(Events.START_CAR, this::startCar);
    super.consumer(Events.STOP_CAR, this::stopCar);
    super.consumer(Events.ECHO, this::echo);
    // Observable<Long> startRequestRepeat = Observable.interval(5, 2,
    // TimeUnit.SECONDS);
    // startRequester=startRequestRepeat.subscribe(l->requestStart());
    super.postStart();
  }

  private void requestStart() {
    if (car == null) {
      super.send(Characters.BOSS_HOGG, "REQUEST_CONFIG", "true");
    } else {
      startRequester.dispose();
    }
  }

  private void echo(Message<JsonObject> message) {
    JsonObject jo = message.body();
    System.out.println("received jsonobject: " + jo);
    String msg = String.format("Thread %d is clustered %s",
        Thread.currentThread().getId(), this.getVertx().isClustered());
    System.out.println(msg);
    // starter.getVertx().eventBus().send(Characters.BOSS_HOGG.getCallsign(),
    // jo);
    super.sendEvent(Characters.ROSCO, Events.ECHO_REPLY, jo);
    System.out.println("send echo reply to " + Characters.ROSCO + " finished");
  }

  private void stopCar(Message<JsonObject> message) {
    if (car == null) {
      LOG.error("No car instance active - can't stop");
      return;
    }
    car.stop();
    starter.undeployVerticle(watchDog);
    starter.undeployVerticle(carVerticle);
    Car.resetInstance();
    car = null;
  }

  private void startCar(Message<JsonObject> message) {
    JsonObject configJo = message.body();
    if (configJo == null) {
      LOG.error("Can't start verticle - configuration message body is null");
      return;
    }
    Environment.from(configJo);
    if (car != null) {
      LOG.info(
          "Car verticle already configured and started - only reconfigured the environment ...");
      return;
    }
    LOG.info(
        "Firing up General Lee vert.x and core controller, tutu tu tu tu tutututu tututu...");

    car = Car.getInstance(); // the one and only to be used also by the
                             // verticle!
    configureShutdownHook(car);
    // Set wheel and speed to neutral.
    car.stop();
    // Command.stop();
    try {
      watchDog = new WatchDog(car);
      carVerticle = new CarVerticle(hostname);
      starter.deployVerticles(watchDog, carVerticle);
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }
    // super.send(Characters.BOSS_HOGG, "started","true");
  }

  protected CmdLineParser parser;
  @Option(name = "-d", aliases = {
      "--debug" }, usage = "debug\ncreate additional debug output if this switch is used")
  protected boolean debug = false;
  @Option(name = "-ch", aliases = { "--hostname" }, usage = "clusterHostname")
  String clusterHostname = null;
  @Option(name = "-ph", aliases = { "--publichost" }, usage = "public hostname")
  String publicHost = null;
  
  private String hostname;

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
   * start me with given command line parameters
   * 
   * @param args
   *          - the command line parameters to use
   * @throws Exception
   */
  public void mainInstance(String... args) {
    try {
      this.parseArguments(args);
    } catch (CmdLineException cle) {
      ErrorHandler.getInstance().handle(cle);
      System.exit(1);
    }
    starter = new ClusterStarter();
    starter.prepare();
    try {
      hostname = InetAddress.getLocalHost().getCanonicalHostName();
    } catch (UnknownHostException e) {
      LOG.error(e.getMessage());
    }
    if (clusterHostname == null) {
      clusterHostname=hostname;
    }
    if (this.publicHost==null) {
      publicHost=hostname;
    }
    String msg=String.format("starting remoteCar on %s setting host to %s and clusterPublicHost to %s",hostname,clusterHostname,publicHost);
    LOG.info(msg);
    EventBusOptions eventBusOptions = starter.getOptions().getEventBusOptions();
    // https://github.com/eclipse-vertx/vert.x/issues/3229
    // https://stackoverflow.com/a/49028531/1497139
    // https://vertx.io/docs/apidocs/io/vertx/core/eventbus/EventBusOptions.html#setClusterPublicHost-java.lang.String-
    eventBusOptions.setHost(clusterHostname);
    eventBusOptions.setClusterPublicHost(publicHost);
    // bootstrap the deployment by deploying me
    try {
      starter.deployVerticles(this);
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }
  }

  private static void configureShutdownHook(Car car) {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        LOG.info("Activating shutdown hook.");
        car.stop();
      }
    });
  }

  /**
   * static main routine
   * 
   * @param args
   */
  public static void main(String... args) {
    RemoteCar remoteCar = new RemoteCar();
    remoteCar.mainInstance(args);
  }

}
