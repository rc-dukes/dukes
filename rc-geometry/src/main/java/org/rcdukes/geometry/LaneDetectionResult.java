package org.rcdukes.geometry;

/**
 * Lane Detection Result
 * @author wf
 *
 */
public class LaneDetectionResult {

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

  /**
   * get a string for the given line's angle
   * @param line (potentially null)
   * @return - the angle String
   */
  public String angleString(Line line) {
    String text=line!=null?String.format("%6.1f°", line.angleDeg()+90):"?";
    return text;
  }
  
  /**
   * get the debug info for me
   * @return - a string with navigation info
   */
  public String debugInfo() {
    String msg=String.format("  left: %s\nmiddle: %s\n right: %s\ncourse: %6.1f°", angleString(left),angleString(middle),angleString(right),Math.toDegrees(courseRelativeToHorizon));
    return msg;
  }
 
}
