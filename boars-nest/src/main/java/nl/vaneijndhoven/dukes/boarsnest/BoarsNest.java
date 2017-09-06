package nl.vaneijndhoven.dukes.boarsnest;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import nl.vaneijndhoven.daisy.Daisy;
import nl.vaneijndhoven.dukes.bosshogg.BossHogg;
import nl.vaneijndhoven.dukes.hazardcounty.Config;
import nl.vaneijndhoven.dukes.hazardcounty.Events;
import nl.vaneijndhoven.dukes.luke.Luke;
import nl.vaneijndhoven.dukes.roscoe.Roscoe;
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
            vertx.deployVerticle(new Roscoe());

            boolean enableAutoPilot = true;

            if (enableAutoPilot) {
                vertx.deployVerticle(new Luke());
                vertx.deployVerticle(new Daisy(), deploymentOptions, async -> {
                    if (async.failed()) {
                        LOG.error("Deploying Daisy 1 failed...");
                        return;
                    }

                    vertx.deployVerticle(new Daisy(), deploymentOptions, result -> {
                        if (result.failed()) {
                            LOG.error("Deploying Daisy 2 failed...");
                            return;
                        }

                        vertx.eventBus().send(Events.STREAMADDED.name(), new JsonObject().put("source", "http://10.9.8.7/html/cam_pic_new.php?time=1472218786342&pDelay=66666"));
                    });

                });
            }

        });
    }

}
