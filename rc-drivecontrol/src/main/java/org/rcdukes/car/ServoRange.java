package org.rcdukes.car;

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
  }
 }
