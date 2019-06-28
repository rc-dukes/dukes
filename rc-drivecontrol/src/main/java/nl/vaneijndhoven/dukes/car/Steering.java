package nl.vaneijndhoven.dukes.car;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vaneijndhoven.dukes.drivecontrol.Car;

/**
 * the steering Servo
 * 
 * @author wf
 *
 */
public class Steering extends Servo {

  SteeringMap steeringMap;

  private static final Logger LOG = LoggerFactory.getLogger(Steering.class);

  private Car car;
  
  /**
   * construct me from the given Steering Map
   * 
   * @param steeringMap
   */
  public Steering(Car car,SteeringMap steeringMap) {
    super(steeringMap);
    this.car=car;
    this.steeringMap = steeringMap;
  }

  public void center() {
    boolean force = false;
    setWheelPosition(steeringMap.center(), force);
  }

  public void forceCenter() {
    boolean force = true;
    setWheelPosition(steeringMap.center(), force);
  }

  public void setWheelPosition(int position) {
    boolean force = false;
    setWheelPosition(position, force);
  }

  private void setWheelPosition(int position, boolean force) {
    if (!car.powerIsOn() && !force) {
      LOG.debug("Not setting servo value; power is off and force is false.");
      return;
    }

    LOG.debug("Setting servo to value " + position);
    super.setServo(position);
  }

  public SteeringMap getSteeringMap() {
    return steeringMap;
  }

}
