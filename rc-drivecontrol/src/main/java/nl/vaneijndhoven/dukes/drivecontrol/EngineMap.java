package nl.vaneijndhoven.dukes.drivecontrol;

import com.bitplan.error.ErrorHandler;

import nl.vaneijndhoven.dukes.car.ServoCommand;
import nl.vaneijndhoven.dukes.common.Config;
import nl.vaneijndhoven.dukes.common.Environment;

/**
 * engine parameters which are vehicle dependent
 *
 */
public class EngineMap extends ServoMap
    implements nl.vaneijndhoven.dukes.car.EngineMap {

  public static int ENGINE_GPIO = 17;
  public static int SPEED_ZERO = 130;
  public static int SPEED_STEP_SIZE = 1;

  public static int MIN_SPEED_REVERSE = SPEED_ZERO - 9;
  public static int MIN_SPEED_FORWARD = SPEED_ZERO + 8;

  public static int MAX_SPEED_REVERSE = SPEED_ZERO - 50;
  public static int MAX_SPEED_FORWARD = SPEED_ZERO + 90;

  public static String ENGINE_ORIENTATION = "-";

  /**
   * configure me from the given ServoCommand
   * 
   * @param servoCommand
   */
  public EngineMap(ServoCommand servoCommand) {
    Environment env = Config.getEnvironment();
    try {
      ENGINE_GPIO = env.getInteger(Config.ENGINE_GPIO);
      SPEED_ZERO = env.getInteger(Config.ENGINE_SPEED_ZERO);
      SPEED_STEP_SIZE = env.getInteger(Config.ENGINE_STEP_SIZE);
      MIN_SPEED_REVERSE = env.getInteger(Config.ENGINE_MIN_SPEED_REVERSE);
      MAX_SPEED_REVERSE = env.getInteger(Config.ENGINE_MAX_SPEED_REVERSE);
      MIN_SPEED_FORWARD = env.getInteger(Config.ENGINE_MIN_SPEED_FORWARD);
      MAX_SPEED_FORWARD = env.getInteger(Config.ENGINE_MAX_SPEED_FORWARD);
      ENGINE_ORIENTATION = env.getString(Config.ENGINE_ORIENTATION);

    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e,
          "you might want to check you settings");
    }
    LOG.info(String.format(
        "Engine gpio: %3d orientation: %s reverse: %3d - %3d forward %3d - %3d zero: %3d  steps:  %3d",
        ENGINE_GPIO,ENGINE_ORIENTATION,MIN_SPEED_REVERSE, MAX_SPEED_REVERSE, MIN_SPEED_FORWARD,
        MAX_SPEED_FORWARD, SPEED_ZERO, SPEED_STEP_SIZE));
    super.configure(ENGINE_GPIO, servoCommand,ENGINE_ORIENTATION.trim().equals("+"));
  }

  @Override
  public int neutral() {
    return SPEED_ZERO;
  }

  @Override
  public int stepSize() {
    return SPEED_STEP_SIZE;
  }

  @Override
  public int minReverse() {
    return MIN_SPEED_REVERSE;
  }

  @Override
  public int maxReverse() {
    return MAX_SPEED_REVERSE;
  }

  @Override
  public int minForward() {
    return MIN_SPEED_FORWARD;
  }

  @Override
  public int maxForward() {
    return MAX_SPEED_FORWARD;
  }
}
