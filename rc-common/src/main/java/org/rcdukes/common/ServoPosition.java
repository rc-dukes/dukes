package org.rcdukes.common;

/**
 * a servo position
 * @author wf
 *
 */
public class ServoPosition {

  double value;
  int servoPos;
  // optional extra info
  public String unit="";
  public String kind="";
  transient public String servoConfig;
  transient public String valueConfig;
  
  public double getValue() {
    return value;
  }
  public void setValue(double value) {
    this.value = value;
  }
  public int getServoPos() {
    return servoPos;
  }
  public void setServoPos(int servoPos) {
    this.servoPos = servoPos;
  }
  
  /**
   * default constructor to allow for JsonObject mapping
   */
  public ServoPosition() {}
  
  /**
   * create a servo position
   * @param servoPos
   * @param value
   */
  public ServoPosition (int servoPos, double value) {
    init(servoPos,value);
  }
  
  /**
   * construct me with pos, value and  unit
   * @param servoPos
   * @param value
   * @param unit
   */
  public ServoPosition(int servoPos, double value, String unit, String kind) {
    init(servoPos,value);
    this.unit=unit;
    this.kind=kind;
  }
  
  /**
   * initialize me from the given environment variables
   * @param servoConfig
   * @param valueConfig
   * @throws Exception 
   */
  public ServoPosition(String servoConfig,String valueConfig) throws Exception {
    this.servoConfig=servoConfig;
    this.valueConfig=valueConfig;
    Environment env = Config.getEnvironment();
    int servoPos=env.getInteger(servoConfig);
    double value=env.getDouble(valueConfig);
    init (servoPos,value);
  }
  
  /**
   * initialize the instance variables
   * @param servoPos
   * @param value
   */
  public void init (int servoPos, double value) {
    this.servoPos=servoPos;
    this.value=value;
  }

  /**
   * convert me to a string
   */
  public String toString() {
    String info = String.format("%5.1f%s (%3d)",
        this.getValue(), this.unit,
        this.getServoPos());
    return info;
  }

}
