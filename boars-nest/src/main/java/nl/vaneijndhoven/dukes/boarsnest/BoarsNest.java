package nl.vaneijndhoven.dukes.boarsnest;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import nl.vaneijndhoven.dukes.bosshogg.BossHogg;
import nl.vaneijndhoven.dukes.hazardcounty.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoarsNest extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(BoarsNest.class);

    public static void main(String... args) throws Exception {
        Config.configureLogging();

        LOG.info("Firing up Boars Nest (UI runner)");

        VertxOptions options = new VertxOptions()
                .setClustered(true)
                .setClusterManager(Config.createHazelcastConfig());

        Vertx.clusteredVertx(options, resultHandler -> {
            Vertx vertx = resultHandler.result();
            DeploymentOptions deploymentOptions = new DeploymentOptions();
            deploymentOptions.setWorker(true);
            vertx.deployVerticle(new BossHogg());
        });
    }

}
