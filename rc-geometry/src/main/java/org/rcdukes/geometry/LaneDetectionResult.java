package org.rcdukes.geometry;

/**
 * Lane Detection Result
 * @author wf
 *
 */
public class LaneDetectionResult {

  public static boolean forceError=false;
  public Double left;
  public Double middle;
  public Double right;
  public Double distanceMiddle;
  public Double distanceLeft;
  public Double distanceRight;
  public Double courseRelativeToHorizon;
  public Double distanceToStoppingZone;
  public Double distanceToStoppingZoneEnd;
  public int frameIndex;
  public long milliTimeStamp;
  
  public void checkError() {
    if (forceError)
      throw new RuntimeException("error forced to debug");
  }
  
  /**
   * get the debug info for me
   * @return - a string with navigation info
   */
  public String debugInfo() {
    String msg=String.format("\n  left: %s\nmiddle: %s\n right: %s\ncourse: %s", Line.angleString(left),Line.angleString(middle),Line.angleString(right),courseRelativeToHorizon==null?"?":Line.angleString(Math.toDegrees(courseRelativeToHorizon)));
    return msg;
  }
 
}
