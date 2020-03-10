package org.rcdukes.car;

import org.rcdukes.common.ServoPosition;

/**
 * a side of a servo e.g. left/right or forward/reverse
 * 
 * @author wf
 *
 */
public class ServoSide {
  ServoPosition min;
  ServoPosition max;
  double polarity;
  String name;

  public ServoPosition getMin() {
    return min;
  }

  public void setMin(ServoPosition min) {
    this.min = min;
  }

  public ServoPosition getMax() {
    return max;
  }

  public void setMax(ServoPosition max) {
    this.max = max;
  }

  public double getPolarity() {
    return polarity;
  }

  public void setPolarity(double polarity) {
    this.polarity = polarity;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * create a servo side
   * @param name
   * @param polarity
   * @param min
   * @param max
   */
  public ServoSide(String name,double polarity,ServoPosition min, ServoPosition max) {
    super();
    this.name=name;
    this.polarity=polarity;
    if (polarity>0) {
      this.min = min;
      this.max = max;
    } else {
      this.min=max;
      this.max=min;
    }
  }

  /**
   * make sure the servo Value stays within the valid bounds
   * @param value
   * @return - the clamped valud
   */
  public double clampValue(double value) {
    if (value < min.getValue())
      return min.getValue();
    else if (value > max.getValue())
      return max.getValue();
    else
      return value;
  }

  /**
   * make sure the servo position stays within the valid bounds
   * @param pos
   * @return the clamped servo position
   */
  public int clampServoPos(long pos) {
    int servoPos=(int) pos;
    if (servoPos < min.getServoPos())
      return min.getServoPos();
    else if (servoPos > max.getServoPos())
      return max.getServoPos();
    else
      return servoPos;
  }

  /**
   * interpolate a servo position based on the given percentage
   * 
   * @param percent
   * @return a servoPosition
   */
  public ServoPosition interpolate(double percent) {
    double factor=this.percentToFactor(percent);
    int pos=interpolateServoPos(factor);
    double value=interpolateValue(factor);
    ServoPosition servoPos = new ServoPosition(pos,value);
    return servoPos;
  }
  
  /**
   * convert the given percent value to a factor
   * @param percent - the percent value
   * @return - the factor to use
   */
  public double percentToFactor(double percent) {
    double factor=Math.abs(percent)/100;
    if (polarity<0)
      factor=1-factor;
    return factor;
  }
  
  /**
   * interpolate the servo position with the given factor
   * @param factor
   * @return - a new servoPos setting
   */
  public int interpolateServoPos(double factor) {
    double posRange = (max.getServoPos() - min.getServoPos());
    double pPos = min.getServoPos() + posRange * factor;
    return clampServoPos(Math.round(pPos));
  }
  
  /**
   * get the value Range
   * @return
   */
  public double valueRange() {
    double valueRange = (max.getValue() - min.getValue());
    return valueRange;
  }
  
  /**
   * interpolate the value with the given factor
   * @param factor
   * @return - the new value
   */
  public double interpolateValue(double factor) {
    double pVal = min.getValue() + valueRange() *factor;
    return clampValue(pVal);
  }
  
  /**
   * interpolate a value from the given servo position
   * @param spos
   * @return - the interpolated value
   */
  public double interpolateValueFromPos(int spos) {
    double posRange = (max.getServoPos() - min.getServoPos());
    double factor=(spos-min.getServoPos())/posRange;
    double result=interpolateValue(factor);
    return result;
  }
  
  /**
   * check whether the serviOis is on this Side
   * @param servoPos
   * @return - true if the servoPos is on this Side
   */
  public boolean isServoPosOnSide(int servoPos) {
    boolean in=false;
    int maxp=Math.max(max.getServoPos(),min.getServoPos());
    int minp=Math.min(max.getServoPos(),min.getServoPos());
    in=servoPos<=maxp && servoPos>=minp;
    return in;
  }
  
  /**
   * check whether the value is on this Side
   * @param value
   * @return - true if the value is on this Side
   */
  public boolean isValueOnSide(double value) {
    boolean in=false;
    double maxv=Math.max(max.getValue(),min.getValue());
    double minv=Math.min(max.getValue(),min.getValue());
    in=value<=maxv && value>=minv;
    return in;
  }

}
