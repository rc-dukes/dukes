package org.rcdukes.car;

import org.rcdukes.common.Config;
import org.rcdukes.common.ServoPosition;
import org.rcdukes.drivecontrol.Car;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * handle the steering of the car from given command messages
 *
 */
class SteeringHandler {

  private static final Logger LOG = LoggerFactory
      .getLogger(SteeringHandler.class);

  private Car car;
  private ServoRangeMap steeringMap;

  private int steerFactor;
  
  /**
   * create a steering handler for the given car
   * 
   * @param car
   */
  SteeringHandler(Car car) {
    this.car = car;
    this.steeringMap=car.getSteering().getSteeringMap(); 
    steerFactor=1; // steeringMap.turnedOrientation()?-1:1;
  }

  /**
   * handle direct servo Messages - the position is to be absolutely set
   * 
   * @param messageBody
   *          containing a double value "position" element
   * @return the new servo position
   */
  ServoPosition handleServoDirect(JsonObject messageBody) {
    LOG.debug("Received direct message for servo: " + messageBody);
    String position = messageBody.getString("position");
    int percent = Double.valueOf(position).intValue()*steerFactor;
    ServoPosition newPosition=this.steeringMap.atPercent(percent);
    car.turn(newPosition);
    return newPosition;
  }
  
  /**
   * handle the given servo Angle
   * @param angleJo
   * @return
   */
  public ServoPosition handleServoAngle(JsonObject angleJo) {
    Double angle = angleJo.getDouble("angle");
    ServoPosition newPosition=this.steeringMap.atValue(angle*steerFactor);
    String msg=String.format("steering %5.1f° for wanted angle %5.1f°", newPosition.getValue(),angle);
    LOG.debug(msg);
    car.turn(newPosition);
    return newPosition;
  }

  /**
   * handle a message for the steering servo
   * 
   * @param messageBody
   *          - containing a position element with "left"/"right" commands
   * @return 
   */
  ServoPosition handleServo(JsonObject messageBody) {
    LOG.debug("Received message for servo: " + messageBody);
    String position = messageBody.getString("position");
    if (Config.POSITION_LEFT.equals(position)) {
      steeringMap.step(-1*steerFactor);
    } else if (Config.POSITION_RIGHT.equals(position)) {
      steeringMap.step(1*steerFactor);
    } else if (Config.POSITION_CENTER.equals(position)) {
      steeringMap.setZero();
    }
    ServoPosition newPosition=steeringMap.getCurrentPosition();
    car.turn(newPosition);
    return newPosition;
  }

}
