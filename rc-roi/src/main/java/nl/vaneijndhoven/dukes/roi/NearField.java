package nl.vaneijndhoven.dukes.roi;

/**
 * full width fractional height Region of Interest filtering lower fraction of
 * image
 *
 */
public class NearField extends ROI {

  /**
   * full width fractional height Region of Interest filtering top fraction of
   * image
   * 
   * @param fraction
   *          - the fraction to filter
   */
  public NearField(double fraction) {
    super(0, 1 - fraction, 1, fraction);
  }

}