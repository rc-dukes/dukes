package org.rcdukes.roi;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;

/**
 * a region of interest relative to the screen
 *
 */
public class ROI {
  double rx, ry, rw, rh;
  String name;

  /**
   * construct me with the given relative
   * 
   * @param rx
   *          - relative x 0-1
   * @param ry
   *          - relative y 0-1
   * @param rw
   *          - relative width 0-1
   * @param rh
   *          - relative height 0-1
   */
  public ROI(String name,double rx, double ry, double rw, double rh) {
    this.name=name;
    this.rx = rx;
    this.ry = ry;
    this.rw = rw;
    this.rh = rh;
  }

  /**
   * create a rectangle based on the given OpenCV Size
   * 
   * @param base
   *          - the base size to use
   * @return - the rectangle
   */
  public Rect roiRect(Size base) {
    double roiX = base.width * rx;
    double roiY = base.height * ry;
    double roiWidth = base.width * rw;
    double roiHeight = base.height * rh;

    Point origin = new Point(roiX, roiY);
    Size roiSize = new Size(roiWidth, roiHeight);

    return new Rect(origin, roiSize);
  }

  /**
   * construct the region of interest image from the given base image
   * 
   * @param image
   * @return the region of interest image
   */
  public Mat region(Mat image) {
    // Use 2-arg constructor to create region image backed by original image
    // data
    Rect roiRect = roiRect(image.size());
    return new Mat(image,roiRect);
  }

}
