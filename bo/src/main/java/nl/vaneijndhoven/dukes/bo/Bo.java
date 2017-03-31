package nl.vaneijndhoven.dukes.bo;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import nl.vaneijndhoven.dukes.car.Engine;
import nl.vaneijndhoven.dukes.car.Steering;
import nl.vaneijndhoven.dukes.car.Car;
import nl.vaneijndhoven.dukes.generallee.EngineMap;
import nl.vaneijndhoven.dukes.generallee.SteeringMap;
import nl.vaneijndhoven.dukes.hazardcounty.Characters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscription;

public class Bo extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(Bo.class);

    private Car car = new Car(new Engine(new EngineMap()), new Steering(new SteeringMap()));
    private SpeedHandler speedHandler = new SpeedHandler(car);
    private SteeringHandler steeringHandler = new SteeringHandler(car);

    @Override
    public void start() {
        LOG.info("Starting Bo (motor and steering output)");

        Subscription subscription = vertx.eventBus().consumer(Characters.BO.getCallsign()).toObservable()
                .doOnNext(x -> LOG.trace("Received instruction"))
                .map(Message::body)
                .cast(String.class)
                .map(JsonObject::new)
//                .map(msg -> new JsonObject(msg))
                .subscribe(message -> {
//                    JsonObject message = (JsonObject)message.body();
                        LOG.trace("Instruction: {}", message);
//                    String type = message.getString("type");
//                    switch (type) {
//                        case "motor":
//                            speedHandler.handleMotor(message);
//                            break;
//                        case "servo":
//                            steeringHandler.handleServo(message);
//                            break;
//                        case "servoDirect":
//                            steeringHandler.handleServoDirect(message);
//                            break;
//                        case "speedDirect":
//                            speedHandler.handleSpeedDirect(message);
//                            break;
//                        case "log":
//                            String logMessage = message.getString("message");
//                            LOG.debug("Received log message: "  + logMessage);
//                            break;
//                        default:
//                            LOG.error("Unknown message type {}", type);
//                    }
        });

        LOG.info("Bo started");
    }
}
