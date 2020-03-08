package org.rcdukes.car;

import java.util.Arrays;

/**
 * a range for a Servo
 * @author wf
 *
 */
public class ServoRange {
  int stepSize;
 
  ServoPosition zeroPosition; 
  ServoSide sideN;
  ServoSide sideP;
  private ServoPosition max;
  private ServoPosition min;
  
  public int getStepSize() {
    return stepSize;
  }
  public void setStepSize(int stepSize) {
    this.stepSize = stepSize;
  }
 
  public ServoPosition getZeroPosition() {
    return zeroPosition;
  }
  public void setZeroPosition(ServoPosition zeroPosition) {
    this.zeroPosition = zeroPosition;
  }
  public ServoSide getSideN() {
    return sideN;
  }
  public void setSideN(ServoSide sideN) {
    this.sideN = sideN;
  }
  public ServoSide getSideP() {
    return sideP;
  }
  public void setSideP(ServoSide sideP) {
    this.sideP = sideP;
  }
 
  /**
   * construct the servo range
   * @param stepSize
   * @param sideN
   * @param zeroPosition
   * @param sideP
   */
  public ServoRange(int stepSize, ServoSide sideN, ServoPosition zeroPosition,
      ServoSide sideP) {
    super();
    this.stepSize = stepSize;
    this.zeroPosition = zeroPosition;
    this.sideN = sideN;
    this.sideP = sideP;
    this.setMinMax();
  }
  
  public void setMinMax() {
    ServoPosition pos[]= {getSideN().getMin(),getSideN().getMax(),getSideP().getMin(),getSideP().getMax()};
    Arrays.sort(pos, (p1,p2) -> Integer.signum(p1.getServoPos()-p2.getServoPos()));
    this.min=pos[0];
    this.max=pos[3];
  }
  
  /**
   * make sure the servo position stays within the valid bounds
   * @param pos
   * @return the clamped servo position
   */
  public int clampServoPos(long pos) {
    int servoPos=(int) pos;
    if (servoPos < min.servoPos)
      return min.servoPos;
    else if (servoPos > max.servoPos)
      return max.servoPos;
    else
      return servoPos;
  }
 }
