package org.rcdukes.car;

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
   * step the given number of steps -1, 1
   * @param servoStep
   */
  public void step(int servoStep);
  /**
   * set the zero/center position
   */
  public void setZero();
}
