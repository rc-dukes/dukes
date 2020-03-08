package org.rcdukes.car;

import org.rcdukes.common.Config;
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
  
  /**
   * create a steering handler for the given car
   * 
   * @param car
   */
  SteeringHandler(Car car) {
    this.car = car;
    this.steeringMap=car.getSteering().getSteeringMap(); 
  }

  /**
   * handle direct servo Messages - the position is to be absolutely set
   * 
   * @param messageBody
   *          containing a double value "position" element
   */
  void handleServoDirect(JsonObject messageBody) {
    LOG.debug("Received direct message for servo: " + messageBody);
    String position = messageBody.getString("position");
    int percent = Double.valueOf(position).intValue();
    car.turn(this.steeringMap.atPercent(percent));
  }

  /**
   * handle a message for the steering servo
   * 
   * @param messageBody
   *          - containing a position element with "left"/"right" commands
   */
  void handleServo(JsonObject messageBody) {
    LOG.debug("Received message for servo: " + messageBody);
    String position = messageBody.getString("position");
    if (Config.POSITION_LEFT.equals(position)) {
      steeringMap.step(-1);
    } else if (Config.POSITION_RIGHT.equals(position)) {
      steeringMap.step(1);
    } else if (Config.POSITION_CENTER.equals(position)) {
      steeringMap.setZero();
    }
    car.turn(steeringMap.getCurrentPosition());
  }

}
