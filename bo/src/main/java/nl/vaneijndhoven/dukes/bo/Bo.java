package nl.vaneijndhoven.dukes.bo;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import nl.vaneijndhoven.dukes.cooter.Engine;
import nl.vaneijndhoven.dukes.cooter.Steering;
import nl.vaneijndhoven.dukes.cooter.car.Car;
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
                .subscribe(message -> {
                    JsonObject messageBody = (JsonObject)message.body();

                    String type = messageBody.getString("type");
                    switch (type) {
                        case "motor":
                            speedHandler.handleMotor(messageBody);
                            break;
                        case "servo":
                            steeringHandler.handleServo(messageBody);
                            break;
                        case "servoDirect":
                            steeringHandler.handleServoDirect(messageBody);
                            break;
                        case "speedDirect":
                            speedHandler.handleSpeedDirect(messageBody);
                            break;
                        case "log":
                            String logMessage = messageBody.getString("message");
                            LOG.debug("Received log message: "  + logMessage);
                            break;
                        default:
                            LOG.error("Unknown message type {}", type);
                    }
        });

        LOG.info("Bo started");
    }
}
