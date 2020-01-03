package nl.vaneijndhoven.dukes.car;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;
import nl.vaneijndhoven.dukes.common.Characters;
import nl.vaneijndhoven.dukes.common.DukesVerticle;
import nl.vaneijndhoven.dukes.drivecontrol.Car;
import rx.Subscription;

/**
 * Motor and steering output aka Bo
 */
public class CarVerticle extends DukesVerticle {
  
  public CarVerticle() {
    super(Characters.BO);
  }

  private Car car = Car.getInstance(); // there is only one car for the time being
  private SpeedHandler speedHandler = new SpeedHandler(car);
  private SteeringHandler steeringHandler = new SteeringHandler(car);

    @Override
    public void start() {
      super.preStart();

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
        super.postStart();
    }
}
