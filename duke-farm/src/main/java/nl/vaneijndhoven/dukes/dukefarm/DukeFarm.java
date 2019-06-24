package nl.vaneijndhoven.dukes.dukefarm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import nl.vaneijndhoven.dukes.bo.Bo;
import nl.vaneijndhoven.dukes.car.Car;
import nl.vaneijndhoven.dukes.car.Command;
import nl.vaneijndhoven.dukes.car.Engine;
import nl.vaneijndhoven.dukes.car.Steering;
import nl.vaneijndhoven.dukes.common.Config;
import nl.vaneijndhoven.dukes.common.Environment;
import nl.vaneijndhoven.dukes.generallee.EngineMap;
import nl.vaneijndhoven.dukes.generallee.SteeringMap;
import nl.vaneijndhoven.dukes.watchdog.WatchDog;

/**
 * Runner to start the cluster on the car
 *
 */
public class DukeFarm {

    private static final Logger LOG = LoggerFactory.getLogger(DukeFarm.class);

    /**
     * start me with given command line parameters
     * @param args - the command line parameters to use
     * @throws Exception
     */
    public static void main(String... args) throws Exception {
        Car car = new Car(new Engine(new EngineMap()), new Steering(new SteeringMap()));

        Config.configureLogging();
        configureShutdownHook(car);

        // Set wheel and speed to neutral.
        car.stop();
//        Command.stop();

        LOG.info("Firing up General Lee vert.x and core controller, tutu tu tu tu tutututu tututu...");

        VertxOptions options = new VertxOptions()
                .setClustered(true)
                .setClusterManager(Config.createHazelcastConfig());

        if (Environment.getInstance().runningOnRaspberryPi()) {
            LOG.info("Running on the Raspberry Pi, activating Vert.x clustering over the network.");
            // activate clustering over the network on the right interface.
            options.setClusterHost(Environment.getInstance().getPiAddress());
        } else {
            LOG.info("Not running on the Raspberry Pi, not activating Vert.x clustering over the network.");
        }

        Vertx.clusteredVertx(options, resultHandler -> {
            Vertx vertx = resultHandler.result();
            DeploymentOptions deploymentOptions = new DeploymentOptions();
            deploymentOptions.setWorker(true);
            vertx.deployVerticle(new WatchDog(), deploymentOptions);
            vertx.deployVerticle(new Bo(), deploymentOptions);
/*
            vertx.deployVerticle(new Daisy(), deploymentOptions);
            vertx.deployVerticle(new Daisy(), deploymentOptions, async -> {

                if (async.failed()) {
                    return;
                }

//                vertx.eventBus().send(Events.STREAMADDED.name(), new JsonObject().put("source", "file://Users/jpoint/Repositories/dukes/daisy/src/main/resources/videos/full_run.mp4"));
            });
            vertx.deployVerticle(new Luke(), async -> {
                if (async.failed()) {
                    return;
                }

//                vertx.eventBus().send(Characters.LUKE.getCallsign() + ":" + Luke.START_DRAG_NAVIGATION,null);
            });*/
//            vertx.deployVerticle(new UncleJesse());
        });
    }

    private static void configureShutdownHook(Car car) {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run() {
                LOG.info("Activating shutdown hook.");
                car.stop();
                Command.statusLedOff();
            }
        });
    }

}
