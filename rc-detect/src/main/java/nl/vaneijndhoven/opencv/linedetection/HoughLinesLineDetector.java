package nl.vaneijndhoven.opencv.linedetection;

import org.rcdukes.geometry.Line;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.rcdukes.detectors.LineDetector;
import org.rcdukes.video.ImageCollector;

import java.util.*;

import static java.util.Optional.of;

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
  transient private Optional<ImageCollector> collector;

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
    Mat lines = detectMat(image);

    Set<Line> lineObjects = new HashSet<>();

    for (int x = 0; x < lines.rows(); x++) {
      Line line = new Line(lines.get(x, 0));
      lineObjects.add(line);
    }

    return lineObjects;
  }

  /**
   * detect lines
   * 
   * @param image
   * @return
   */
  private Mat detectMat(Mat image) {
    Mat lines = new Mat();
    if (probabilistic) {
      // HoughLinesP(image, lines, rho, theta, threshold, minLineLength,
      // maxLineGap)
      Imgproc.HoughLinesP(image, lines, rho, theta, threshold, minLineLength,
          maxLineGap);
    } else {
      Imgproc.HoughLines(image, lines, rho, theta, threshold);
    }
    collector.ifPresent(coll -> coll.lines(lines));
    return lines;
  }

  public HoughLinesLineDetector withImageCollector(ImageCollector collector) {
    this.collector = of(collector);
    return this;
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
