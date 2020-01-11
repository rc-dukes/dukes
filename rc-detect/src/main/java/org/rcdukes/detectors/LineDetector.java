package org.rcdukes.detectors;

import org.opencv.core.Mat;

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
  LineDetectionResult detect(Mat image);
}
