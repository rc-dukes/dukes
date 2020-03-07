package org.rcdukes.car;

/**
 * settings for a Servo
 * @author wf
 *
 */
public interface ServoMap {
  // gpioPin that controls the servo
  int gpioPin();
  // command interface e.g. ServoBlaster or Adafruit servo shield
  ServoCommand servoCommand();
  // return true if the orientation is turned (+)
  boolean turnedOrientation();
  public ServoPosition getCurrentPosition();
  public void setCurrentPosition(ServoPosition currentPosition);
  /**
   * configure the given servoMap
   * @param gpioPin - the gpio pin to use
   * @param servoCommand
   * @param orientation - do we have to switch left/right or forward/reverse commands +: do not switch -:switch
   */
  public void configure(int gpioPin,ServoCommand servoCommand, String orientation);
  
}
