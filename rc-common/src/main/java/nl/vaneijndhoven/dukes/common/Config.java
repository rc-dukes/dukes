package nl.vaneijndhoven.dukes.common;

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
 * Configuration for 
 *   Logging and HazelCastClusterManager
 *   Environment 
 *
 */
public class Config {
  private static final Logger LOG = LoggerFactory.getLogger(Config.class);
  public static final String WEBCONTROL_PORT = "webcontrol.port";
  public static final String IMAGEVIEW_PORT = "imageview.port";
  public static final String REMOTECAR_HOST = "remotecar.host";
  public static final String CAMERA_URL = "camera.url";

  /**
   * configure the logging
   */
  public static void configureLogging() {
    System.setProperty(
        io.vertx.core.logging.LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME,
        SLF4JLogDelegateFactory.class.getName());

    LoggerContext logConfig = (LoggerContext) LoggerFactory.getILoggerFactory();
    logConfig.getLogger("ROOT").setLevel(Level.TRACE);

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
