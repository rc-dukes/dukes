package org.rcdukes.car;

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
    if (value < min.value)
      return min.value;
    else if (value > max.value)
      return max.value;
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
    if (servoPos < min.servoPos)
      return min.servoPos;
    else if (servoPos > max.servoPos)
      return max.servoPos;
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
    double posRange = (max.servoPos - min.servoPos);
    double pPos = min.servoPos + posRange * factor;
    return clampServoPos(Math.round(pPos));
  }
  
  /**
   * interpolate the value with the given factor
   * @param factor
   * @return - the new value
   */
  public double interpolateValue(double factor) {
    double valueRange = (max.value - min.value);
    double pVal = min.value + valueRange *factor;
    return clampValue(pVal);
  }
  
  /**
   * interpolate a value from the given servo position
   * @param spos
   * @return - the interpolated value
   */
  public double interpolateValueFromPos(int spos) {
    double posRange = (max.servoPos - min.servoPos);
    double factor=(spos-min.servoPos)/posRange;
    double result=interpolateValue(factor);
    return result;
  }
  
  /**
   * check whether the value is on/in this Side
   * @param servoPos
   * @return - true if the value is within min and max
   */
  public boolean isIn(int servoPos) {
    boolean in=servoPos<=max.servoPos && servoPos>=min.servoPos;
    return in;
  }

}
