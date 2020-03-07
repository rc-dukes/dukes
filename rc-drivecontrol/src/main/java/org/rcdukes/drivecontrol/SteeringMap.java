package org.rcdukes.drivecontrol;

import org.rcdukes.car.ServoCommand;
import org.rcdukes.car.ServoPosition;
import org.rcdukes.car.ServoRange;
import org.rcdukes.car.ServoSide;
import org.rcdukes.common.Config;
import org.rcdukes.common.Environment;
import org.rcdukes.error.ErrorHandler;

/**
 * steering map which is vehicle dependent
 *
 */
public class SteeringMap extends ServoRangeMap {
 
  /**
   * steering mapping
   * 
   * @param servoCommand
   */
  public SteeringMap(ServoCommand servoCommand) {
    Environment env = Config.getEnvironment();
    try {
      int WHEEL_STEP_SIZE = env.getInteger(Config.WHEEL_STEP_SIZE);
      int WHEEL_GPIO = env.getInteger(Config.WHEEL_GPIO);
      String WHEEL_ORIENTATION = env.getString(Config.WHEEL_ORIENTATION);
      ServoPosition leftMax = new ServoPosition(Config.WHEEL_MAX_LEFT, Config.WHEEL_MAX_LEFT_ANGLE);
      ServoPosition leftMin = new ServoPosition(Config.WHEEL_CENTER, Config.ZERO);
      ServoPosition rightMax = new ServoPosition(Config.WHEEL_MAX_RIGHT, Config.WHEEL_MAX_RIGHT_ANGLE);
      ServoPosition rightMin = new ServoPosition(Config.WHEEL_CENTER,Config.ZERO);
      ServoSide sideP=new ServoSide("right",1,rightMin,rightMax);
      ServoSide sideN=new ServoSide("left",-1,leftMin,leftMax);
      ServoPosition center = new ServoPosition(Config.WHEEL_CENTER,Config.ZERO);
      setRange(new ServoRange(WHEEL_STEP_SIZE, sideN, center,sideP));
      setUnit("°");
      setName("Steering");
      super.configure(WHEEL_GPIO, servoCommand,
          WHEEL_ORIENTATION);
      check();
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e,
          "you might want to check you settings");
    }
  }

}
