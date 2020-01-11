package org.rcdukes.detectors;

import java.util.Collection;

import org.opencv.core.Mat;
import org.rcdukes.geometry.Line;

/**
 * a line detection result
 * @author wf
 *
 */
public class LineDetectionResult {
  private Collection<Line> lines;
  private Mat linesImage;
  /**
   * @return the lines
   */
  public Collection<Line> getLines() {
    return lines;
  }
  /**
   * @param lines the lines to set
   */
  public void setLines(Collection<Line> lines) {
    this.lines = lines;
  }
  /**
   * @return the linesImage
   */
  public Mat getLinesImage() {
    return linesImage;
  }
  /**
   * @param linesImage the linesImage to set
   */
  public void setLinesImage(Mat linesImage) {
    this.linesImage = linesImage;
  }
}
