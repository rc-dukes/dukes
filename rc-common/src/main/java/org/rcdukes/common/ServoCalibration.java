package org.rcdukes.common;

import java.util.Map;
import java.util.TreeMap;

import io.vertx.core.json.JsonObject;

/**
 * holds value for servo calibration
 * @author wf
 *
 */
public class ServoCalibration implements POJO {

  private String vehicle;

  private String type;
  private String unit;

  Map<Integer, Double> valueMap;

  /**
   * a servomap
   */
  public ServoCalibration() {
    valueMap = new TreeMap<Integer, Double>();
  }
  
  public static ServoCalibration fromJo(JsonObject jo) {
    return jo.mapTo(ServoCalibration.class);
  }
  
  public String toString() {
    return this.asJson();
  }

  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type
   *          the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * @return the unit
   */
  public String getUnit() {
    return unit;
  }

  /**
   * @param unit
   *          the unit to set
   */
  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Map<Integer, Double> getValueMap() {
    return valueMap;
  }

  public void setValueMap(Map<Integer, Double> valueMap) {
    this.valueMap = valueMap;
  }
}
