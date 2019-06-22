package nl.vaneijndhoven.dukes.boarsnest;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import nl.vaneijndhoven.daisy.Daisy;
import nl.vaneijndhoven.dukes.bosshogg.BossHogg;
import nl.vaneijndhoven.dukes.hazardcounty.Config;
import nl.vaneijndhoven.dukes.hazardcounty.Environment;
import nl.vaneijndhoven.dukes.hazardcounty.Events;
import nl.vaneijndhoven.dukes.luke.Luke;
import nl.vaneijndhoven.dukes.roscoe.Roscoe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoarsNest extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(BoarsNest.class);

    /**
     * start the the cluser
     * @param args
     * @throws Exception
     */
    public static void main(String... args) throws Exception {
        Config.configureLogging();
        String cameraUrl=Environment.getInstance().getCameraUrl();
        
        LOG.info("Firing up Boars Nest (UI runner) using cameraUrl "+cameraUrl);

        VertxOptions options = new VertxOptions()
                .setClustered(true)
                .setClusterManager(Config.createHazelcastConfig())
                .setBlockedThreadCheckInterval(1000*60*60);

        Vertx.clusteredVertx(options, resultHandler -> {
            Vertx vertx = resultHandler.result();
            DeploymentOptions deploymentOptions = new DeploymentOptions();
            deploymentOptions.setWorker(true);
            deploymentOptions.setMultiThreaded(true);
            vertx.deployVerticle(new BossHogg());
            vertx.deployVerticle(new Roscoe());

            boolean enableAutoPilot = true;

            if (enableAutoPilot) {
                vertx.deployVerticle(new Luke());
                vertx.deployVerticle(new Daisy(), deploymentOptions, async -> {
                    vertx.eventBus().send(Events.STREAMADDED.name(), new JsonObject().put("source", cameraUrl));

//                    if (async.failed()) {
//                        LOG.error("Deploying Daisy 1 failed...");
//                        return;
//                    }
//
//                    vertx.deployVerticle(new Daisy(), deploymentOptions, result -> {
//                        if (result.failed()) {
//                            LOG.error("Deploying Daisy 2 failed...");
//                            return;
//                        }

//                    });

                });
            }

        });
    }

}
