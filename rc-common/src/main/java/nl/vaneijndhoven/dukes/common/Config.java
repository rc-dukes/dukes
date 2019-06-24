package nl.vaneijndhoven.dukes.common;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.LoggerFactory;

/**
 * Configuration for Logging and HazelCastClusterManager
 *
 */
public class Config {

    /**
     * configure the logging
     */
    public static void configureLogging() {
        System.setProperty(io.vertx.core.logging.LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory.class.getName());

        LoggerContext logConfig = (LoggerContext) LoggerFactory.getILoggerFactory();
        logConfig.getLogger("ROOT").setLevel(Level.TRACE);

        logConfig.getLogger("io.vertx").setLevel(Level.INFO);
        logConfig.getLogger("com.hazelcast").setLevel(Level.INFO);
        logConfig.getLogger("com.hazelcast.nio.tcp").setLevel(Level.ERROR);
        logConfig.getLogger("io.netty").setLevel(Level.INFO);
    }

    /**
     * create a ClusterManager with a default hazelcast configuration using slf4j
     * @return - the ClusterManager
     */
    public static ClusterManager createHazelcastConfig() {
        com.hazelcast.config.Config hazelcastConfig = new com.hazelcast.config.Config();
        hazelcastConfig.setProperty("hazelcast.logging.type", "slf4j");
        ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);
        return mgr;
    }

}
