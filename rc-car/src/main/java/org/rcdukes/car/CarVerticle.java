package org.rcdukes.car;

import org.rcdukes.common.Characters;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.drivecontrol.Car;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Subscription;

/**
 * Motor and steering output aka Bo
 */
public class CarVerticle extends DukesVerticle {

  private String hostname;

  /**
   * start the car verticle for the given hostname
   * @param hostname
   */
  public CarVerticle(String hostname) {
    super(Characters.BO);
    this.hostname=hostname;
  }

  private Car car = Car.getInstance(); // there is only one car for the time
                                       // being
  private SpeedHandler speedHandler = new SpeedHandler(car);
  private SteeringHandler steeringHandler = new SteeringHandler(car);

  @Override
  public void start() {
    super.preStart();
    super.send(Characters.BOARS_NEST, "getconfig",hostname);
    Subscription subscription = vertx.eventBus()
        .consumer(Characters.BO.getCallsign()).toObservable()
        .doOnNext(x -> LOG.trace("Received instruction")).map(Message::body)
        .cast(JsonObject.class).subscribe(message -> {
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
            LOG.debug("Received log message: " + logMessage);
            break;
          default:
            LOG.error("Unknown message type {}", type);
          }
        });
    super.postStart();
  }
}
