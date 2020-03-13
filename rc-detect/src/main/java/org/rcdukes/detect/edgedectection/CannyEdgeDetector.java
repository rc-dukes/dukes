package org.rcdukes.detect.edgedectection;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.rcdukes.detectors.EdgeDetector;

/**
 * Canny edge detector
 * @see <a href="https://docs.opencv.org/trunk/da/d22/tutorial_py_canny.html">OpenCV Canny Edge Detection </a>
 */
public class CannyEdgeDetector implements EdgeDetector {

  double threshold1 = 60;
  double threshold2 = 150;
  int apertureSize = 3;
  boolean l2gradient = false;
  
  public double getThreshold1() {
    return threshold1;
  }

  public void setThreshold1(double threshold1) {
    this.threshold1 = threshold1;
  }

  public double getThreshold2() {
    return threshold2;
  }

  public void setThreshold2(double threshold2) {
    this.threshold2 = threshold2;
  }

  public int getApertureSize() {
    return apertureSize;
  }

  public void setApertureSize(int apertureSize) {
    this.apertureSize = apertureSize;
  }

  public boolean isL2gradient() {
    return l2gradient;
  }

  public void setL2gradient(boolean l2gradient) {
    this.l2gradient = l2gradient;
  }

  public CannyEdgeDetector() {
  }

  /**
   * construct me with the given parameters
   * @param threshold1
   * @param threshold2
   * @param apertureSize
   * @param l2gradient
   */
  public CannyEdgeDetector(double threshold1, double threshold2,
      int apertureSize, boolean l2gradient) {
    this();
    this.threshold1 = threshold1;
    this.threshold2 = threshold2;
    this.apertureSize = apertureSize;
    this.l2gradient = l2gradient;
  }

  @Override
  public Mat detect(Mat image) {
    Mat imgEdges = new Mat();
    Imgproc.Canny(image, imgEdges, threshold1, threshold2, apertureSize,
        l2gradient);
    return imgEdges;
  }

}
