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

  /**
   * force the car to neutral
   */
  public void forceInNeutral() {
    boolean force = true;
    neutral(force);
  }
  
  /**
   * set the speed to the given servo Position
   * @param speed
   */
  public void setSpeed(ServoPosition speed) {
    boolean force = false;
    setSpeed(speed, force);
  }

  /**
   * switch to neutral
   * @param force
   */
  public void neutral(boolean force) {
    setSpeed(engineMap.getRange().getZeroPosition(), force);
  }

  /**
   * 
   * @param speed
   * @param force
   */
  private void setSpeed(ServoPosition speed, boolean force) {
    if (!car.powerIsOn() && !force) {
      String msg=String.format("Not setting motor value to %s; power is off and force is false.",speed);
      LOG.debug(msg);
      return;
    }
    LOG.debug("Setting motor to value " + speed);
    super.setServo(speed.servoPos);
  }
  
  public ServoRangeMap getEngineMap() {
    return engineMap;
  }
}
