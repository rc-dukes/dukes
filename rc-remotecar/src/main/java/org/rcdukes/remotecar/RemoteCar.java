package org.rcdukes.remotecar;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

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

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;

/**
 * Runner to start the remote cluster on the car
 *
 */
public class RemoteCar extends DukesVerticle {

  private ClusterStarter starter;
  String hostname = "?";
  private Car car;
  private Disposable startRequester;

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
    Observable<Long> startRequestRepeat = Observable.interval(5, 2, TimeUnit.SECONDS);
    startRequester=startRequestRepeat.subscribe(l->requestStart());
    super.postStart();
  }
  
  private void requestStart() {
    if (car==null) {
      super.send(Characters.BOSS_HOGG, "REQUEST_CONFIG","true");
    } else {
      startRequester.dispose();
    }
  }

  private void startCar(Message<JsonObject> message) {
    JsonObject configJo = message.body();
    if (configJo==null) {
      LOG.error("Can't start verticle - configuration message body is null");
      return;
    }
    Environment.from(configJo);
    if (car!=null) {
      LOG.info("Car verticle already configured and started - only reconfigured the environment ...");
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
      starter.deployVerticles(new WatchDog(car), new CarVerticle(hostname));
      super.send(Characters.BOSS_HOGG, "started","true");
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }
  }

  /**
   * start me with given command line parameters
   * 
   * @param args
   *          - the command line parameters to use
   * @throws Exception
   */
  public void mainInstance(String... args) {
    starter = new ClusterStarter();
    starter.prepare();
    try {
      hostname = InetAddress.getLocalHost().getCanonicalHostName();
    } catch (UnknownHostException e) {
      LOG.error(e.getMessage());
    }
    LOG.info("starting remoteCar on host " + hostname);
    starter.getOptions().getEventBusOptions().setHost(hostname);
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
