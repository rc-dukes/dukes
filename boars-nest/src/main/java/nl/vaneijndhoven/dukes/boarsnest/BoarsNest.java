package nl.vaneijndhoven.dukes.boarsnest;

import com.hazelcast.config.Config;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import nl.vaneijndhoven.dukes.bosshogg.BossHogg;
import nl.vaneijndhoven.dukes.hazardcounty.Characters;
import nl.vaneijndhoven.dukes.unclejesse.UncleJesse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscription;

public class BoarsNest extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(BoarsNest.class);

    public static void main(String... args) throws Exception {
        configureLogging();

        LOG.info("Firing up Boars Nest (UI runner)");

        VertxOptions options = new VertxOptions()
                .setClustered(true)
                .setClusterManager(createHazelcastConfig());

        Vertx.clusteredVertx(options, resultHandler -> {
            Vertx vertx = resultHandler.result();
            DeploymentOptions deploymentOptions = new DeploymentOptions();
            deploymentOptions.setWorker(true);
            vertx.deployVerticle(new BossHogg());
        });
    }

    private static void configureLogging() {
        System.setProperty(io.vertx.core.logging.LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory.class.getName());

//        LoggerContext logConfig = (LoggerContext) LoggerFactory.getILoggerFactory();
//        logConfig.getLogger("io.vertx").setLevel(Level.INFO);
//        logConfig.getLogger("com.hazelcast").setLevel(Level.ERROR);
//        logConfig.getLogger("io.netty").setLevel(Level.ERROR);
//
//        logConfig.getLogger("ROOT").setLevel(Level.INFO);
//        logConfig.getLogger("nl.revolution.dukes").setLevel(Level.DEBUG);

    }


    private static ClusterManager createHazelcastConfig() {
        Config hazelcastConfig = new Config();
        hazelcastConfig.setProperty("hazelcast.logging.type", "slf4j");
        ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);
        return mgr;

    }

}
