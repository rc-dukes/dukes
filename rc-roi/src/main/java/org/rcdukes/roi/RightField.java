package org.rcdukes.roi;

/**
 * full height limited width Region of Interest filtering the right part of the
 * Image by a given fraction
 *
 */
public class RightField extends ROI {

  /**
   * create a filtered left right part of the image by the given fraction
   * 
   * @param fraction
   */
  public RightField(double fraction) {
    super(1 - fraction, 0, fraction, 1);
  }
}
