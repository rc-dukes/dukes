package org.rcdukes.car;

import org.rcdukes.drivecontrol.Car;

/**
 * drive control for the engine
 */
public class Engine extends Servo {

  private ServoRangeMap engineMap;
  private Car car;

  /**
   * configure the engine from the given mapping and car
   * @param mapping - servo settings to use
   * @param car - the car
   */
  public Engine(Car car,ServoRangeMap engineMap) {
    super(engineMap);
    this.engineMap=engineMap;
    this.car=car;
  }

  public void neutral() {
    boolean force = false;
    neutral(force);
  }

  public void forceInNeutral() {
    boolean force = true;
    neutral(force);
  }

  public void neutral(boolean force) {
    setSpeed(engineMap.getRange().getZeroPosition(), force);
  }

  public void setSpeed(ServoPosition speed) {
    boolean force = false;
    setSpeed(speed, force);
  }

  private void setSpeed(ServoPosition speed, boolean force) {
    if (!car.powerIsOn() && !force) {
      LOG.debug("Not setting motor value; power is off and force is false.");
      return;
    }

    LOG.debug("Setting motor to value " + speed);
    super.setServo(speed.servoPos);
  }
  
  public ServoRangeMap getEngineMap() {
    return engineMap;
  }
}
