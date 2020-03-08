package org.rcdukes.drivecontrol;

import org.rcdukes.car.ServoCommand;
import org.rcdukes.car.ServoPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * servo gpioPin and command
 * 
 * @author wf
 *
 */
public abstract class ServoMap implements org.rcdukes.car.ServoMap {
  protected static final Logger LOG = LoggerFactory
      .getLogger(ServoMap.class);

  protected ServoCommand servoCommand;
  protected int gpioPin;
  protected boolean turnedOrientation;
  ServoPosition currentPosition;

  public ServoPosition getCurrentPosition() {
    return currentPosition;
  }

  public void setCurrentPosition(ServoPosition currentPosition) {
    this.currentPosition = currentPosition;
  }
  
  /**
   * set a new Position
   * @param newPosition
   */
  @Override
  public ServoPosition newPosition(ServoPosition newPosition) {
    ServoPosition sp=new ServoPosition(newPosition.getServoPos(),newPosition.getValue());
    this.setCurrentPosition(sp);
    return sp;
  }

  @Override
  public int gpioPin() {
    return gpioPin;
  }

  @Override
  public ServoCommand servoCommand() {
    return servoCommand;
  }

  @Override
  public void configure(int gpioPin, ServoCommand servoCommand,
      String orientation) {
    boolean turnedOrientation = orientation.trim().equals("-");
    this.gpioPin = gpioPin;
    this.servoCommand = servoCommand;
    this.turnedOrientation = turnedOrientation;
    configureOrientation();
  }

  @Override
  public boolean turnedOrientation() {
    return turnedOrientation;
  }

}
