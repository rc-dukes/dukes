package nl.vaneijndhoven.opencv.video;

//import ch.qos.logback.classic.Level;
//import ch.qos.logback.classic.LoggerContext;
//import com.hazelcast.config.Config;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.core.spi.cluster.ClusterManager;
//import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
//import org.slf4j.LoggerFactory;

public class LoggingUtils {

    public static void configureLogging() {
//        System.setProperty(io.vertx.core.logging.LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory.class.getName());
//
//        LoggerContext logConfig = (LoggerContext) LoggerFactory.getILoggerFactory();
//        logConfig.getLogger("io.vertx").setLevel(Level.INFO);
//        logConfig.getLogger("com.hazelcast").setLevel(Level.INFO);
//        logConfig.getLogger("io.netty").setLevel(Level.ERROR);
//
//        logConfig.getLogger("ROOT").setLevel(Level.INFO);
//        logConfig.getLogger("nl.revolution.dukes").setLevel(Level.DEBUG);

    }


    public static ClusterManager createHazelcastConfig() {
//        Config hazelcastConfig = new Config();
//        hazelcastConfig.setProperty("hazelcast.logging.type", "slf4j");
//        ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);
//        return mgr;
        return null;

    }

}
