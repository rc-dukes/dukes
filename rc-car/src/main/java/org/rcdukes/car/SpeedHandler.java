package org.rcdukes.car;

import org.rcdukes.drivecontrol.Car;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Car speed handling
 *
 */
class SpeedHandler {

  private static final Logger LOG = LoggerFactory.getLogger(SpeedHandler.class);
  private Car car;
  private ServoRangeMap engineMap;

  /**
   * create a speed handler for the given car
   * 
   * @param car
   */
  public SpeedHandler(Car car) {
    this.car = car;
    this.engineMap=car.getEngine().getEngineMap();
  }


  /**
   * handle a motor message
   * 
   * @param messageBody
   *          - containing an "up"/"down" element
   */
  void handleMotor(JsonObject messageBody) {
    LOG.debug("Received message for motor: " + messageBody);
    String speed = messageBody.getString("speed");
    int stepFactor=engineMap.turnedOrientation()?-1:1;
    if ("up".equals(speed)) {
      this.engineMap.step(1*stepFactor);
      car.drive(engineMap.getCurrentPosition());
    } else if ("down".equals(speed)) {
      this.engineMap.step(-1*stepFactor);
      car.drive(engineMap.getCurrentPosition());
    } else if ("stop".equals(speed)) {
      this.engineMap.setZero();
      car.stop();
    } else if ("brake".equals(speed)) {
      performBrake();
    }
  }

  private void performBrake() {
    LOG.debug("engaging braking sequence");

    try {

      setSpeedDirect("-1");

      Thread.sleep(100);
      setSpeedDirect("-2");

      Thread.sleep(300);
      changeSpeed("-1");

      Thread.sleep(500);
      changeSpeed("stop");

      Thread.sleep(1000);
      changeSpeed("stop");

    } catch (InterruptedException e) {
      LOG.error("Error in brake sequence", e);
    }

  }

  private void setSpeedDirect(String speed) {
    JsonObject message = new JsonObject();
    message.put("speed", speed);
    handleSpeedDirect(message);
  }

  private void changeSpeed(String speed) {
    JsonObject message = new JsonObject();
    message.put("speed", speed);
    handleMotor(message);
  }

  void handleSpeedDirect(JsonObject messageBody) {
    LOG.debug("Received direct message for speed: " + messageBody);
    String speed = messageBody.getString("speed");
    int percent = Double.valueOf(speed).intValue();
    engineMap.atPercent(percent);
    car.drive(engineMap.getCurrentPosition());
  }

}
