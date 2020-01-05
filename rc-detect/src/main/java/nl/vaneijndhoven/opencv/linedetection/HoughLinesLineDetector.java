package nl.vaneijndhoven.opencv.linedetection;

import nl.vaneijndhoven.dukes.geometry.Line;
import nl.vaneijndhoven.opencv.tools.ImageCollector;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.*;

import static java.util.Optional.of;

public class HoughLinesLineDetector implements LineDetector {

  private double rho = 1;
  private double theta = Math.PI / 180; // By choosing this value lines sloping
                                        // left to right will be < 0 radian,
                                        // while lines sloping right to left
                                        // will be > 0 radian.
  private int threshold = 70;
  private double minLineLength = 20;
  private double maxLineGap = 10;
  private Optional<ImageCollector> collector;
  private boolean probabilistic = true;

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

  public HoughLinesLineDetector(Config cfg) {
    this(cfg.isProbabilistic(), cfg.getRho(), cfg.getTheta(),
        cfg.getThreshold(), cfg.getMinLineLength(), cfg.getMaxLineGap());
  }

  @Override
  public Collection<Line> detect(Mat image) {
    Mat lines = detectMat(image);

    Set<Line> lineObjects = new HashSet<>();

    for (int x = 0; x < lines.rows(); x++) {
      Line line = new Line(lines.get(x, 0));
      lineObjects.add(line);
    }

    lines.release();
    lines = null;

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
      // HoughLinesP(image, lines, rho, theta, threshold, minLineLength, maxLineGap)
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

  public static class Config {

    private boolean probabilistic = true;
    private double rho = 1;
    private double theta = Math.PI / 180; // By choosing this value lines
                                          // sloping left to right will be < 0
                                          // radian, while lines sloping right
                                          // to left will be > 0 radian.
    private int threshold = 70;
    private double minLineLength = 20;
    private double maxLineGap = 10;

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
}
