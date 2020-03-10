package org.rcdukes.geometry;

/**
 * Lane Detection Result
 * @author wf
 *
 */
public class LaneDetectionResult {

  public static boolean forceError=false;
  public Line left;
  public Line middle;
  public Line right;
  public Double distanceMiddle;
  public Double distanceLeft;
  public Double distanceRight;
  public Double courseRelativeToHorizon;
  public Double distanceToStoppingZone;
  public Double distanceToStoppingZoneEnd;
  public int frameIndex;
  public long milliTimeStamp;
  // any angle correction induced by car physics
  public Double angleOffset;
  
  public void checkError() {
    if (forceError)
      throw new RuntimeException("error forced to debug");
  }

  /**
   * get a string for the given line's angle
   * @param line (potentially null)
   * @return - the angle String
   */
  public String lineAngleString(Line line) {
    String text=angleString(line==null?null:line.angleDeg90());
    return text;
  }
  
  /**
   * get a string for the given angle
   * @param angle (potentially null)
   * @return - the angle String
   */
  public String angleString(Double angle) {
    String text=angle!=null?String.format("%6.1f°", angle):"?";
    return text;
  }
  
  /**
   * get the debug info for me
   * @return - a string with navigation info
   */
  public String debugInfo() {
    String msg=String.format("\n  left: %s\nmiddle: %s\n right: %s\ncourse: %s", lineAngleString(left),lineAngleString(middle),lineAngleString(right),courseRelativeToHorizon==null?"?":angleString(Math.toDegrees(courseRelativeToHorizon)));
    return msg;
  }
 
}
