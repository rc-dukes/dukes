package org.rcdukes.app;

import java.util.concurrent.TimeUnit;

import org.rcdukes.action.Navigator;
import org.rcdukes.action.StraightLaneNavigator;
import org.rcdukes.common.Characters;
import org.rcdukes.common.ClusterStarter;
import org.rcdukes.common.Config;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.EventbusLogger;
import org.rcdukes.common.Events;
import org.rcdukes.common.WebStarter;
import org.rcdukes.error.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.JsonObject;

/**
 * JavaFx Application Verticle
 *
 */
public class AppVerticle extends DukesVerticle {
  boolean debug = false;
  protected static final Logger LOG = LoggerFactory
      .getLogger(AppVerticle.class);
  private static AppVerticle instance;
  private ClusterStarter starter;
  private int HEARTBEAT_INTERVAL = 150; // send a heartbeat every 150 millisecs
  private Disposable heartBeatSubscription;
  private SimulatorImageFetcher simulatorImageFetcher;
  private Navigator navigator;

  public Navigator getNavigator() {
    return navigator;
  }

  public void setNavigator(Navigator navigator) {
    this.navigator = navigator;
  }

  /**
   * JavaFX Application verticle
   * 
   * @param eventbusLogger
   */
  public AppVerticle(EventbusLogger eventbusLogger) {
    super(Characters.UNCLE_JESSE);
    super.setEventbusLogger(eventbusLogger);
  }
  
  public void enableNavigator() {
    navigator=new StraightLaneNavigator();
    navigator.setSender(this);
  }
  
  public void stopNavigator() {
    navigator=null;
  }

  @Override
  public void start() throws Exception {
    super.preStart();
    int port = Config.getEnvironment().getInteger(Config.WEBCONTROL_PORT);
    WebStarter webStarter = new WebStarter(vertx, port);
    webStarter.mountEventBus(".*", ".*");
    webStarter.startHttpServer();

    JsonObject startjo = new JsonObject();
    startjo.put("started", this.character.name());
    setSimulatorImageFetcher(new SimulatorImageFetcher());
    consumer(Characters.ROSCO, Events.SIMULATOR_IMAGE,
        getSimulatorImageFetcher()::receiveSimulatorImage);
    super.postStart();
  }

  /***
   * send a command of the given type with the given name and value to the given
   * character via vert.x
   * 
   * @param character
   * @param type
   * @param name
   * @param value
   */
  public void sendCarCommand(Characters character, String type, String name,
      String value) {
    if (getStatus() != Status.started) {
      String msg = String.format(
          "Can't send %s:%s car command AppVerticle not started yet", name,
          value);
      throw new IllegalStateException(msg);
    }
    JsonObject jo = new JsonObject();
    jo.put("type", type);
    if (name != null)
      jo.put(name, value);
    if (debug) {
      String msg = String.format("car command %s %s=%s", type,
          name == null ? "?" : name, value == null ? "?" : value);
      LOG.info(msg);
    }
    super.send(character, jo);
  }

  /**
   * send the given speed command
   * 
   * @param speed
   */
  public void sendSpeedCommand(String speed) {
    this.sendCarCommand(Characters.BO, "motor", "speed", speed);
  }

  /**
   * send the given wheel command
   * 
   * @param position
   */
  public void sendWheelCommand(String position) {
    this.sendCarCommand(Characters.BO, "servo", "position", position);
  }

  /**
   * start this verticle
   */
  public void startUp() {
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
   * get instance singleton
   * 
   * @param eventbusLogger
   * @return - the instance
   */
  public static AppVerticle getInstance(EventbusLogger eventbusLogger) {
    if (instance == null) {
      instance = new AppVerticle(eventbusLogger);
      instance.startUp();
    }
    return instance;
  }

  /**
   * switch on the heartBeat
   * @param on
   */
  public void heartBeat(boolean on) {
    if (on) {
      heartBeatSubscription = Observable
          .timer(HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS).repeat()
          .subscribeOn(Schedulers.newThread()).subscribe(e -> {
            sendHeartBeat();
          });
    } else {
      heartBeatSubscription.dispose();
      heartBeatSubscription = null;
    }
  }

  /**
   * send a heartbeat
   */
  public void sendHeartBeat() {
    if (getStatus() == Status.started)
      this.sendCarCommand(Characters.FLASH, "heartbeat", null, null);
  }

  /**
   * @return the simulatorImageFetcher
   */
  public SimulatorImageFetcher getSimulatorImageFetcher() {
    return simulatorImageFetcher;
  }

  /**
   * @param simulatorImageFetcher the simulatorImageFetcher to set
   */
  public void setSimulatorImageFetcher(SimulatorImageFetcher simulatorImageFetcher) {
    this.simulatorImageFetcher = simulatorImageFetcher;
  }
}
