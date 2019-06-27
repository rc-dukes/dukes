package nl.vaneijndhoven.dukes.drivecontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vaneijndhoven.dukes.common.Config;
import nl.vaneijndhoven.dukes.common.Environment;

/**
 * steering map which is vehicle dependent
 *
 */
public class SteeringMap implements nl.vaneijndhoven.dukes.car.SteeringMap {
  private static final Logger LOG = LoggerFactory.getLogger(SteeringMap.class);
  // TODO - defaults are overridden anyway and only fit original RC car used in
  // 2017
  public static int WHEEL_CENTER = 163;
  public static int WHEEL_STEP_SIZE = 5;
  public static int WHEEL_MAX_LEFT = 130;
  public static int WHEEL_MAX_RIGHT = 190;

  public SteeringMap() throws Exception {
    Environment env = Config.getEnvironment();
    WHEEL_CENTER = env.getInteger(Config.WHEEL_CENTER);
    WHEEL_STEP_SIZE = env.getInteger(Config.WHEEL_STEP_SIZE);
    WHEEL_MAX_LEFT = env.getInteger(Config.WHEEL_MAX_LEFT);
    WHEEL_MAX_RIGHT = env.getInteger(Config.WHEEL_MAX_RIGHT);
    LOG.info(String.format("Wheel %3d <- %3d -> %3d steps:  %3d",
        WHEEL_MAX_LEFT, WHEEL_CENTER, WHEEL_MAX_RIGHT, WHEEL_STEP_SIZE));
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
