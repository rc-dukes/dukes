package org.rcdukes.video;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

/**
 * filter colors in a given range
 * 
 * @author wf
 *
 */
public class ColorFilter {
  private Scalar minColor;
  private Scalar maxColor;

  /**
   * set minimum color with relative rgb values
   * 
   * @param r
   *          - red from 0.0 to 1.0
   * @param g
   *          - green from 0.0 to 1.0
   * @param b
   *          - blue from 0.0 to 1.0
   */
  public void setMinColorRGB(double r, double g, double b) {
    this.minColor = new Scalar(b * 255, g * 255, r * 255);
  }
  
  /**
   * set the minimum rgb color
   * @param r red from 0 to 255
   * @param g green from 0 to 255
   * @param b blue from 0 to 255
   */
  public void setMinColorRGB(int r, int g, int b) {
    this.minColor=new Scalar(b,g,r);
  }

  /**
   * set maximum color with rgb values
   * 
   * @param r
   * @param g
   * @param b
   */
  public void setMaxColorRGB(double r, double g, double b) {
    this.maxColor = new Scalar(b * 255, g * 255, r * 255);
  }
  
  /**
   * set the maximum rgb color
   * @param r red from 0 to 255
   * @param g green from 0 to 255
   * @param b blue from 0 to 255
   */
  public void setMaxColorRGB(int r, int g, int b) {
    this.maxColor=new Scalar(b,g,r);
  }

  /**
   * filter by the min and max Colors (if set)
   * 
   * @param image
   *          - the image to filter
   * @return - an image with colors in the given range or the original if not
   *         both minColor and maxColor have been set
   */
  public Mat filter(Mat image) {
    Mat imgColorFiltered = image;
    if (minColor != null && maxColor != null) {
      // https://docs.opencv.org/3.4/da/d97/tutorial_threshold_inRange.html
      // https://stackoverflow.com/questions/36693348/java-opencv-core-inrange-input-parameters
      Mat imgColorMask = new Mat();
      Core.inRange(image, minColor, maxColor, imgColorMask);
      imgColorFiltered = new Mat();
      Core.bitwise_and(image, image, imgColorFiltered, imgColorMask);
    }
    return imgColorFiltered;
  }
}
