package nl.vaneijndhoven.dukes.car;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import nl.vaneijndhoven.dukes.car.Engine;
import nl.vaneijndhoven.dukes.car.Steering;
import nl.vaneijndhoven.dukes.common.Characters;
import nl.vaneijndhoven.dukes.car.Car;
import nl.vaneijndhoven.dukes.generallee.EngineMap;
import nl.vaneijndhoven.dukes.generallee.SteeringMap;
import rx.Subscription;

/**
 * Motor and steering output aka Bo
 */
public class CarVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(CarVerticle.class);

    private Car car = new Car(new Engine(new EngineMap()), new Steering(new SteeringMap()));
    private SpeedHandler speedHandler = new SpeedHandler(car);
    private SteeringHandler steeringHandler = new SteeringHandler(car);

    @Override
    public void start() {
        LOG.info("Starting Car Verticle Bo (motor and steering output)");

        Subscription subscription = vertx.eventBus().consumer(Characters.BO.getCallsign()).toObservable()
                .doOnNext(x -> LOG.trace("Received instruction"))
                .map(Message::body)
                .cast(JsonObject.class)
                .subscribe(message -> {
                    LOG.trace("Instruction: {}", message);
                    String type = message.getString("type");
                    switch (type) {
                        case "motor":
                            speedHandler.handleMotor(message);
                            break;
                        case "servo":
                            steeringHandler.handleServo(message);
                            break;
                        case "servoDirect":
                            steeringHandler.handleServoDirect(message);
                            break;
                        case "speedDirect":
                            speedHandler.handleSpeedDirect(message);
                            break;
                        case "log":
                            String logMessage = message.getString("message");
                            LOG.debug("Received log message: "  + logMessage);
                            break;
                        default:
                            LOG.error("Unknown message type {}", type);
                    }
        });

        LOG.info("CarVerticle Bo started");
    }
}
