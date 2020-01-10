package org.rcdukes.drivecontrol;

import org.rcdukes.car.ServoCommand;
import org.rcdukes.common.Config;
import org.rcdukes.common.Environment;
import org.rcdukes.error.ErrorHandler;

/**
 * steering map which is vehicle dependent
 *
 */
public class SteeringMap extends ServoMap
    implements org.rcdukes.car.SteeringMap {
  // TODO - defaults are overridden anyway and only fit original RC car used in
  // 2017
  public static int WHEEL_CENTER = 163;
  public static int WHEEL_STEP_SIZE = 5;
  public static int WHEEL_MAX_LEFT = 130;
  public static int WHEEL_MAX_RIGHT = 190;
  public static int WHEEL_GPIO = 18;
  public static String WHEEL_ORIENTATION = "-";

  public SteeringMap(ServoCommand servoCommand) {
    Environment env = Config.getEnvironment();
    try {
      WHEEL_CENTER = env.getInteger(Config.WHEEL_CENTER);
      WHEEL_STEP_SIZE = env.getInteger(Config.WHEEL_STEP_SIZE);
      WHEEL_MAX_LEFT = env.getInteger(Config.WHEEL_MAX_LEFT);
      WHEEL_MAX_RIGHT = env.getInteger(Config.WHEEL_MAX_RIGHT);
      WHEEL_GPIO = env.getInteger(Config.WHEEL_GPIO);
      WHEEL_ORIENTATION = env.getString(Config.WHEEL_ORIENTATION);
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e,
          "you might want to check you settings");
    }
    LOG.info(String.format(
        "Wheel gpio: %3d orientation: %s range: %3d <- %3d -> %3d steps:  %3d", WHEEL_GPIO,
        WHEEL_ORIENTATION,WHEEL_MAX_LEFT, WHEEL_CENTER, WHEEL_MAX_RIGHT, WHEEL_STEP_SIZE));
    super.configure(WHEEL_GPIO, servoCommand,WHEEL_ORIENTATION.trim().equals("+"));
  }

  @Override
  public int center() {
    return WHEEL_CENTER;
  }

  @Override
  public int stepSize() {
    return WHEEL_STEP_SIZE;
  }

  @Override
  public int maxLeft() {
    return WHEEL_MAX_LEFT;
  }

  @Override
  public int maxRight() {
    return WHEEL_MAX_RIGHT;
  }
}
