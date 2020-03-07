package org.rcdukes.car;

import org.rcdukes.drivecontrol.Car;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * the steering Servo
 * 
 * @author wf
 *
 */
public class Steering extends Servo {

  ServoRangeMap steeringMap;

  private static final Logger LOG = LoggerFactory.getLogger(Steering.class);

  private Car car;
  
  /**
   * construct me from the given Steering Map
   * 
   * @param steeringMap
   */
  public Steering(Car car,ServoRangeMap steeringMap) {
    super(steeringMap);
    this.car=car;
    this.steeringMap = steeringMap;
  }

  public void center() {
    boolean force = false;
    setWheelPosition(steeringMap.getRange().getZeroPosition(), force);
  }

  public void forceCenter() {
    boolean force = true;
    setWheelPosition(steeringMap.getRange().getZeroPosition(), force);
  }

  /** 
   * set the wheel Position to the given position
   */
  public void setWheelPosition(ServoPosition position) {
    boolean force = false;
    setWheelPosition(position, force);
  }

  private void setWheelPosition(ServoPosition position, boolean force) {
    if (!car.powerIsOn() && !force) {
      LOG.debug("Not setting servo value; power is off and force is false.");
      return;
    }

    LOG.debug("Setting servo to value " + position);
    super.setServo(position.servoPos);
  }

  public ServoRangeMap getSteeringMap() {
    return steeringMap;
  }

}
