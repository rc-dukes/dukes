package org.rcdukes.car;

import org.rcdukes.common.Characters;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.Events;
import org.rcdukes.common.ServoPosition;
import org.rcdukes.drivecontrol.Car;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Subscription;

/**
 * Motor and steering output aka Bo
 */
public class CarVerticle extends DukesVerticle {

  /**
   * start the car verticle
   */
  public CarVerticle() {
    super(Characters.BO);
  }

  private Car car = Car.getInstance(); // there is only one car for the time
                                       // being
  private SpeedHandler speedHandler = new SpeedHandler(car);
  private SteeringHandler steeringHandler = new SteeringHandler(car);

  @Override
  public void start() {
    super.preStart();
    Subscription subscription = vertx.eventBus()
        .consumer(Characters.BO.getCallsign()).toObservable()
        .doOnNext(x -> LOG.trace("Received instruction")).map(Message::body)
        .cast(JsonObject.class).subscribe(message -> handleCarMessage(message));
    super.postStart();
  }

  private void handleCarMessage(JsonObject message) {
    LOG.trace("Instruction: {}", message);
    String type = message.getString("type");
    ServoPosition newPosition = null;
    switch (type) {
    case "motor":
      newPosition = speedHandler.handleMotor(message);
      newPosition.kind="motor";
      break;
    case "servo":
      newPosition = steeringHandler.handleServo(message);
      newPosition.kind="steering";
      break;
    case "servoAngle":
      newPosition=steeringHandler.handleServoAngle(message);
      newPosition.kind="steering";
      break;
    case "servoDirect":
      newPosition = steeringHandler.handleServoDirect(message);
      newPosition.kind="steering";
      break;
    case "speedDirect":
      newPosition = speedHandler.handleSpeedDirect(message);
      newPosition.kind="motor";
      break;
    case "log":
      String logMessage = message.getString("message");
      LOG.debug("Received log message: " + logMessage);
      break;
    default:
      LOG.error("Unknown message type {}", type);
    }
    if (newPosition!=null) {
      JsonObject posReply=JsonObject.mapFrom(newPosition);
      this.sendEvent(Characters.BOSS_HOGG, Events.CAR_POSITION, posReply);
    }

  }
}
