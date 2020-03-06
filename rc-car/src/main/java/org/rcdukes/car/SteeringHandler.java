package org.rcdukes.car;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.rcdukes.car.SteeringMap;
import org.rcdukes.common.Config;
import org.rcdukes.drivecontrol.Car;

/**
 * handle the steering of the car from given command messages
 *
 */
class SteeringHandler {

  private static final Logger LOG = LoggerFactory
      .getLogger(SteeringHandler.class);

  private int currentWheelPosition;
  private double currentWheelAngle;
  private Car car;

  /**
   * interpolated between the minimum and maximum value with the given percentage
   * @param min
   * @param max
   * @param percentage
   * @return - the interpolated value
   */
  protected int interpolate(int min, int max, int percentage) {
    int range = max - min;
    int interpolated = min + ((range * Math.abs(percentage)) / 100);
    return interpolated;
  }
  
  /**
   * create a steering handler for the given car
   * 
   * @param car
   */
  SteeringHandler(Car car) {
    this.car = car;
    this.currentWheelPosition = car.getSteering().getSteeringMap().center();
    this.currentWheelAngle=0;
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

    int positionPercentage = Double.valueOf(position).intValue();

    SteeringMap steeringMap = car.getSteering().getSteeringMap();
    if (!steeringMap.turnedOrientation()) {
      positionPercentage=-positionPercentage;
    }
    double relativePosition=Math.abs(positionPercentage)/100;
    if (positionPercentage < 5) {
      int range = steeringMap.center() - steeringMap.maxLeft();
      currentWheelPosition = steeringMap.center()
          - ((range * Math.abs(positionPercentage)) / 100);
      currentWheelAngle=steeringMap.maxLeftAngle()*relativePosition;
      LOG.debug("directPos = " + position + ", range = " + range + ", newPos = "
          + currentWheelPosition);
    } else if (positionPercentage > 5) {
      int range = steeringMap.maxRight() - steeringMap.center();
      currentWheelPosition = steeringMap.center()
          + ((range * Math.abs(positionPercentage) )/ 100);
      currentWheelAngle=steeringMap.maxRightAngle()*relativePosition;
      LOG.debug("directPos = " + position + ", range = " + range + ", newPos = "
          + currentWheelPosition);
    } else {
      currentWheelPosition = steeringMap.center();
    }

    if (currentWheelPosition < steeringMap.maxLeft()) {
      currentWheelPosition = steeringMap.maxLeft();
    }
    if (currentWheelPosition > steeringMap.maxRight()) {
      currentWheelPosition = steeringMap.maxRight();
    }
    LOG.debug("about to set current wheel pos to " + currentWheelPosition);
    turnWrapper(currentWheelPosition);
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

    SteeringMap steeringMap = car.getSteering().getSteeringMap();

    if (Config.POSITION_LEFT.equals(position)) {
      currentWheelPosition = currentWheelPosition - steeringMap.stepSize();
      if (currentWheelPosition < steeringMap.maxLeft()) {
        currentWheelPosition = steeringMap.maxLeft();
      }
    } else if (Config.POSITION_RIGHT.equals(position)) {
      currentWheelPosition = currentWheelPosition + steeringMap.stepSize();
      if (currentWheelPosition > steeringMap.maxRight()) {
        currentWheelPosition = steeringMap.maxRight();
      }
    } else if (Config.POSITION_CENTER.equals(position)) {
      currentWheelPosition = steeringMap.center();
    }
    turnWrapper(currentWheelPosition);
  }

  private void turnWrapper(int currentWheelPosition) {
    SteeringMap steeringMap = car.getSteering().getSteeringMap();
    if (steeringMap.turnedOrientation()) {
      int diff=currentWheelPosition-steeringMap.center();
      car.turn(steeringMap.center()-diff);
    } else {
      car.turn(currentWheelPosition);
    }
  }

}
