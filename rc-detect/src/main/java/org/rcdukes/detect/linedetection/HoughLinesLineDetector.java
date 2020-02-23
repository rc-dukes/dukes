package org.rcdukes.detect.linedetection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.rcdukes.detectors.LineDetector;
import org.rcdukes.geometry.Line;

/**
 * Hough Lines Line Detector
 *
 */
public class HoughLinesLineDetector implements LineDetector {

  double rho = 1;
  double theta = Math.PI / 180; // By choosing this value lines sloping
                                // left to right will be < 0 radian,
                                // while lines sloping right to left
                                // will be > 0 radian.
  int threshold = 70;
  double minLineLength = 20;
  double maxLineGap = 10;
  boolean probabilistic = true;
 
  /**
   * default constructor
   */
  public HoughLinesLineDetector() {
    
  }
  
  /**
   * construct me
   * 
   * @param rho
   * @param theta
   * @param threshold
   * @param minLineLength
   * @param maxLineGap
   */
  public HoughLinesLineDetector(boolean probabilistic, double rho, double theta,
      int threshold, double minLineLength, double maxLineGap) {
    this.probabilistic = probabilistic;
    this.rho = rho;
    this.theta = theta;
    this.threshold = threshold;
    this.minLineLength = minLineLength;
    this.maxLineGap = maxLineGap;
  }

  @Override
  public Collection<Line> detect(Mat image) {
    Mat houghLinesMat=detectMat(image);
    
    
    Set<Line> lines = new HashSet<Line>();
    for (int x = 0; x < houghLinesMat.rows(); x++) {
      Line line = Line.fromHough(probabilistic,image.width(),image.height(),houghLinesMat.get(x, 0));
      lines.add(line);
    }
    houghLinesMat.release();
    return lines;
  }

  /**
   * detect lines
   * 
   * @param image
   * @return a Mat with encoded lines see 
   * <a href="https://docs.opencv.org/3.4/d9/db0/tutorial_hough_lines.html">OpenCV tutorial hough lines</a>
   */
  private Mat detectMat(Mat image) {
    Mat lineMat = new Mat();
    // see 
    if (probabilistic) {
      Imgproc.HoughLinesP(image, lineMat, rho, theta, threshold, minLineLength,
          maxLineGap);
    } else {
      Imgproc.HoughLines(image, lineMat, rho, theta, threshold);
    }
    return lineMat;
  }

  public double getRho() {
    return rho;
  }

  public void setRho(double rho) {
    this.rho = rho;
  }

  public double getTheta() {
    return theta;
  }

  public void setTheta(double theta) {
    this.theta = theta;
  }

  public int getThreshold() {
    return threshold;
  }

  public void setThreshold(int threshold) {
    this.threshold = threshold;
  }

  public double getMinLineLength() {
    return minLineLength;
  }

  public void setMinLineLength(double minLineLength) {
    this.minLineLength = minLineLength;
  }

  public double getMaxLineGap() {
    return maxLineGap;
  }

  public void setMaxLineGap(double maxLineGap) {
    this.maxLineGap = maxLineGap;
  }

  /**
   * @return the probabilistic
   */
  public boolean isProbabilistic() {
    return probabilistic;
  }

  /**
   * @param probabilistic
   *          the probabilistic to set
   */
  public void setProbabilistic(boolean probabilistic) {
    this.probabilistic = probabilistic;
  }

}
