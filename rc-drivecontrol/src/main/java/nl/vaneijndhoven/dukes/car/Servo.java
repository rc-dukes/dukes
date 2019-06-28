package nl.vaneijndhoven.dukes.car;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * common base class for Servos
 * @author wf
 *
 */
public class Servo {
 
  protected static final Logger LOG = LoggerFactory.getLogger(Servo.class);
  protected ServoMap servoMap;
  
  /**
   * construct this servo with the given Command interface and gpioPin
   * @param servoMap - configuration for interface an pin
   */
  public Servo(ServoMap servoMap) {
    this.servoMap=servoMap;
  }
  
  /**
   * set the servo to the given value
   * @param value - the value to use
   */
  public void setServo(int value) {
    // servo command and pin are configurable
    this.servoMap.servoCommand().setServo(this.servoMap.gpioPin(), value);
  }
  
}
