package org.rcdukes.drivecontrol;

import org.rcdukes.car.ServoCommand;
import org.rcdukes.car.ServoRange;
import org.rcdukes.car.ServoSide;
import org.rcdukes.error.ErrorHandler;

import org.rcdukes.common.Config;
import org.rcdukes.common.Environment;
import org.rcdukes.common.ServoPosition;

/**
 * engine parameters which are vehicle dependent
 *
 */
public class EngineMap extends ServoRangeMap {

  /**
   * configure me from the given ServoCommand
   * 
   * @param servoCommand
   */
  public EngineMap(ServoCommand servoCommand) {
    Environment env = Config.getEnvironment();
    try {
      int ENGINE_GPIO = env.getInteger(Config.ENGINE_GPIO);
      int SPEED_STEP_SIZE = env.getInteger(Config.ENGINE_STEP_SIZE);
      String ENGINE_ORIENTATION = env.getString(Config.ENGINE_ORIENTATION);
      super.configure(ENGINE_GPIO, servoCommand,ENGINE_ORIENTATION);
      ServoPosition minforward = new ServoPosition(Config.ENGINE_MIN_SPEED_FORWARD,Config.ENGINE_MIN_VELOCITY_FORWARD);
      ServoPosition maxforward = new ServoPosition(Config.ENGINE_MAX_SPEED_FORWARD,Config.ENGINE_MAX_VELOCITY_FORWARD);
      ServoPosition minreverse = new ServoPosition(Config.ENGINE_MIN_SPEED_REVERSE,Config.ENGINE_MIN_VELOCITY_REVERSE);
      ServoPosition maxreverse = new ServoPosition(Config.ENGINE_MAX_SPEED_REVERSE,Config.ENGINE_MAX_VELOCITY_REVERSE);
      ServoSide sideN=new ServoSide("reverse",-1,minreverse,maxreverse);  
      ServoSide sideP=new ServoSide("forward",1,minforward,maxforward);
      ServoPosition neutral = new ServoPosition(Config.ENGINE_SPEED_ZERO,Config.ZERO);
      setRange(new ServoRange(SPEED_STEP_SIZE, sideN, neutral, sideP));
      setName("Engine");
      setUnit(" m/s");
      check();
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e,
          "you might want to check you settings");
    }
  }

}
