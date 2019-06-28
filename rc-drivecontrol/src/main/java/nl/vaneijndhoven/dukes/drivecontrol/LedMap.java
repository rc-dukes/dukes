package nl.vaneijndhoven.dukes.drivecontrol;

import com.bitplan.error.ErrorHandler;

import nl.vaneijndhoven.dukes.car.ServoCommand;
import nl.vaneijndhoven.dukes.common.Config;
import nl.vaneijndhoven.dukes.common.Environment;

/**
 * configure the LED with the given parameters
 * @author wf
 *
 */
public class LedMap extends ServoMap
    implements nl.vaneijndhoven.dukes.car.LedMap {
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
    super.configure(LED_GPIO, servoCommand,false);
  }

  @Override
  public int ledOff() {
    return LED_OFF;
  }

  @Override
  public int ledOn() {
    return LED_ON;
  }
 
}
