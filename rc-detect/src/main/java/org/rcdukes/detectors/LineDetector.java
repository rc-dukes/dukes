package org.rcdukes.detectors;

import org.opencv.core.Mat;

import org.rcdukes.geometry.Line;

import java.util.Collection;

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
