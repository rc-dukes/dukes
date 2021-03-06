package org.rcdukes.common;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration for Logging and HazelCastClusterManager Environment
 *
 */
public class Config {
  private static final Logger LOG = LoggerFactory.getLogger(Config.class);
  public static final String WEBCONTROL_PORT = "webcontrol.port";
  public static final String IMAGEVIEW_PORT = "imageview.port";
  public static final String REMOTECAR_HOST = "remotecar.host";
  public static final String CAMERA_URL = "camera.url";
  // watchdog configuration
  public static final String WATCHDOG_HEARTBEAT_INTERVAL_MS = "watchdog.heartbeat.interval.ms";
  public static final String WATCHDOG_MAX_MISSED_BEATS = "watchdog.max.missed.beats";

  // wheel configuration
  public static final String WHEEL_GPIO = "wheel.gpio";
  public static final String WHEEL_ORIENTATION="wheel.orientation";
  public static final String WHEEL_CENTER = "wheel.center";
  public static final String WHEEL_STEP_SIZE = "wheel.stepsize";
  public static final String WHEEL_MAX_LEFT = "wheel.max.left";
  public static final String WHEEL_MAX_RIGHT = "wheel.max.right";
  public static final String WHEEL_MAX_LEFT_ANGLE = "wheel.max.left.angle";
  public static final String WHEEL_MAX_RIGHT_ANGLE = "wheel.max.right.angle";

  // led configuration
  public static final String LED_ON = "led.on";
  public static final String LED_OFF = "led.off";
  public static final String LED_GPIO = "led.gpio";
  // engine configuration
  public static final String ENGINE_GPIO = "engine.gpio";
  public static final String ENGINE_ORIENTATION="engine.orientation";
  public static final String ENGINE_SPEED_ZERO = "engine.speed.zero";
  public static final String ENGINE_STEP_SIZE = "engine.stepsize";
  public static final String ENGINE_MIN_SPEED_REVERSE = "engine.min.speed.reverse";
  public static final String ENGINE_MAX_SPEED_REVERSE = "engine.max.speed.reverse";
  public static final String ENGINE_MIN_SPEED_FORWARD = "engine.min.speed.forward";
  public static final String ENGINE_MAX_SPEED_FORWARD = "engine.max.speed.forward";
  public static final String ENGINE_MIN_VELOCITY_FORWARD = "engine.min.velocity.forward";
  public static final String ENGINE_MAX_VELOCITY_FORWARD = "engine.max.velocity.forward";
  public static final String ENGINE_MIN_VELOCITY_REVERSE = "engine.min.velocity.reverse";
  public static final String ENGINE_MAX_VELOCITY_REVERSE = "engine.max.velocity.reverse";
  // Commands
  public static final String POSITION_LEFT = "left";
  public static final String POSITION_RIGHT = "right";
  public static final String POSITION_CENTER = "center";
  public static final String SERVO_COMMAND = "servo.command";
  public static final String ZERO = "zero";

  /**
   * configure the logging
   */
  public static void configureLogging() {
    System.setProperty(
        io.vertx.core.logging.LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME,
        SLF4JLogDelegateFactory.class.getName());

    LoggerContext logConfig = (LoggerContext) LoggerFactory.getILoggerFactory();
    logConfig.getLogger("ROOT").setLevel(Level.TRACE);
    logConfig.getLogger("nl.vaneijndhoven.dukes.imageview").setLevel(Level.INFO);
    logConfig.getLogger("io.vertx").setLevel(Level.INFO);
    logConfig.getLogger("com.hazelcast").setLevel(Level.INFO);
    logConfig.getLogger("com.hazelcast.nio.tcp").setLevel(Level.ERROR);
    logConfig.getLogger("io.netty").setLevel(Level.INFO);
  }

  /**
   * create a ClusterManager with a default hazelcast configuration using slf4j
   * 
   * @return - the ClusterManager
   */
  public static ClusterManager createHazelcastConfig() {
    com.hazelcast.config.Config hazelcastConfig = new com.hazelcast.config.Config();
    hazelcastConfig.setProperty("hazelcast.logging.type", "slf4j");
    ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);
    return mgr;
  }

  /**
   * get my ip addresses
   * 
   * @return - my ip addresses
   */
  public static List<String> getMyIpAddresses() {
    try {
      return Collections.list(NetworkInterface.getNetworkInterfaces()).stream()
          .map(iface -> Collections.list(iface.getInetAddresses()))
          .flatMap(Collection::stream).map(InetAddress::getHostAddress)
          .collect(Collectors.toList());
    } catch (SocketException e) {
      LOG.error("Error while determining IP addresses: ", e);
      return null;
    }
  }

  /**
   * get the Environment
   * 
   * @return an instance of the Enviroment interface
   */
  public static Environment getEnvironment() {
    return Environment.getInstance();
  }

}
