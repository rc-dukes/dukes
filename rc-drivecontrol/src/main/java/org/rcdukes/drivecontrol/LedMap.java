package org.rcdukes.drivecontrol;

import org.rcdukes.car.ServoCommand;

import org.rcdukes.error.ErrorHandler;

import org.rcdukes.common.Config;
import org.rcdukes.common.Environment;

/**
 * configure the LED with the given parameters
 * @author wf
 *
 */
public class LedMap extends ServoMap
    implements org.rcdukes.car.LedMap {
  private static int LED_ON = 250;
  private static int LED_OFF = 0;
  private static int LED_GPIO= 24;
  
  /**
   * construct me from the given ServoCommand
   * @param servoCommand
   */
  public LedMap(ServoCommand servoCommand) {
    Environment env = Config.getEnvironment();
    try {
      LED_ON = env.getInteger(Config.LED_ON);
      LED_OFF = env.getInteger(Config.LED_OFF);
      LED_GPIO = env.getInteger(Config.LED_GPIO);
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e,"you might want to check you settings");
    }
    LOG.info(String.format("LED gpio: %3d off: %3d on:  %3d",
        LED_GPIO,LED_OFF,LED_ON));
    super.configure(LED_GPIO, servoCommand,"+");
  }

  @Override
  public int ledOff() {
    return LED_OFF;
  }

  @Override
  public int ledOn() {
    return LED_ON;
  }
  
  /**
   * not needed here
   */
  @Override
  public void configureOrientation() {
    
  }
 
}
