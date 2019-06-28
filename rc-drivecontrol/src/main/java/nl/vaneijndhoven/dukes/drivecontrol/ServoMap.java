package nl.vaneijndhoven.dukes.drivecontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vaneijndhoven.dukes.car.ServoCommand;

/**
 * servo gpioPin and command
 * @author wf
 *
 */
public class ServoMap implements nl.vaneijndhoven.dukes.car.ServoMap {
  protected static final Logger LOG = LoggerFactory.getLogger(SteeringMap.class);
  
  protected ServoCommand servoCommand;
  protected int gpioPin;
  
  @Override
  public int gpioPin() {
    return gpioPin;
  }

  @Override
  public ServoCommand servoCommand() {
    return servoCommand;
  }

  @Override
  public void configure(int gpioPin, ServoCommand servoCommand) {
    this.gpioPin=gpioPin;
    this.servoCommand=servoCommand;
  }

}
