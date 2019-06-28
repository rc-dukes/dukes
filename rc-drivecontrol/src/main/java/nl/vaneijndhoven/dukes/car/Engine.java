package nl.vaneijndhoven.dukes.car;

import nl.vaneijndhoven.dukes.drivecontrol.Car;

/**
 * drive control for the engine
 */
public class Engine extends Servo {

  private EngineMap mapping;
  private Car car;

  /**
   * configure the engine from the given mapping and car
   * @param mapping - servo settings to use
   * @param car - the car
   */
  public Engine(Car car,EngineMap mapping) {
    super(mapping);
    this.mapping = mapping;
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
    setSpeed(mapping.neutral(), force);
  }

  public void setSpeed(int speed) {
    boolean force = false;
    setSpeed(speed, force);
  }

  private void setSpeed(int speed, boolean force) {
    if (!car.powerIsOn() && !force) {
      LOG.debug("Not setting motor value; power is off and force is false.");
      return;
    }

    LOG.debug("Setting motor to value " + speed);
    super.setServo(speed);
  }

  public void setMapping(EngineMap mapping) {
    this.mapping = mapping;
  }

  public EngineMap getEngineMap() {
    return mapping;
  }
}
