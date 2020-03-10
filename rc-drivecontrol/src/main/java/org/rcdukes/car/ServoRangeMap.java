package org.rcdukes.car;

import org.rcdukes.common.ServoPosition;

/**
 * a range based map
 * @author wf
 *
 */
public interface ServoRangeMap extends ServoMap {
  /**
   * get the range for this map
   * @return - the range
   */
  public ServoRange getRange();
  /**
   * return a servo position for the given percentual value
   * @param percent
   * @return the ServoPosition
   */
  public ServoPosition atPercent(double percent);
  
  /**
   * return a servo position close to the given value
   * @param value
   * @return - the servoPosition
   */
  public ServoPosition atValue(double value);
  /**
   * step the given number of steps -1, 1
   * @param servoStep
   */
  public void step(int servoStep);
  /**
   * set the zero/center position
   */
  public void setZero();
 
}
