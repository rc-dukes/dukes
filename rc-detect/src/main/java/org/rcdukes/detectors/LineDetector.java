package org.rcdukes.detectors;

import java.util.Collection;

import org.opencv.core.Mat;
import org.rcdukes.geometry.Line;

/**
 * detector for lines
 */
public interface LineDetector {
  /**
   * detect lines from the given image
   * 
   * @param image
   * @return - the lines found
   */
  Collection<Line> detect(Mat image);
}
