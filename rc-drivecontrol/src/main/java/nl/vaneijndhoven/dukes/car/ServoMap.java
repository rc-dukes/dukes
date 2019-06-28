package nl.vaneijndhoven.dukes.car;

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
  
  /**
   * configure the given servoMap
   * @param gpioPin
   * @param servoCommand
   */
  public void configure(int gpioPin,ServoCommand servoCommand);
}
