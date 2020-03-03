package org.rcdukes.geometry;

import java.util.HashMap;
import java.util.Map;

/**
 * Lane Detection Result
 * @author wf
 *
 */
public class LaneDetectionResult {

  public Double angle;
  public Double distanceMiddle;
  public Double distanceLeft;
  public Double distanceRight;
  public Double courseRelativeToHorizon;
  public Double distanceToStoppingZone;
  public Double distanceToStoppingZoneEnd;
  public Lane lane;

  /**
   * convert me to a Map
   * @return a map with my values
   */
  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("lane", lane);
    putIfNumber("angle", angle, map);
    putIfNumber("distanceMiddle", distanceMiddle, map);
    putIfNumber("distanceLeft", distanceLeft, map);
    putIfNumber("distanceRight", distanceRight, map);
    putIfNumber("distanceToStoppingZone", distanceToStoppingZone, map);
    putIfNumber("distanceToStoppingZoneEnd", distanceToStoppingZoneEnd, map);
    putIfNumber("courseRelativeToHorizon", courseRelativeToHorizon, map);
    return map;
  }
  
  /**
   * add the given number 
   * @param key
   * @param number
   * @param result
   */
  private void putIfNumber(String key, double number,
      Map<String, Object> result) {
    if (Double.isNaN(number)) {
      return;
    }
    result.put(key, number);
  }
  
  public String debugInfo() {
    String msg=String.format(" angle: %3.1f°\ncourse:%3.1f%%", angle,courseRelativeToHorizon);
    return msg;
  }
 
}
