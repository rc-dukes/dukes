package org.rcdukes.roi;

/**
 * full height limited width Region of Interest
 * filtering the left part of the Image by a given 
 * fraction
 *
 */
public class LeftField extends ROI {

    /**
     * create a filtered left part of the image by the given fraction
     * @param fraction
     */
    public LeftField(double fraction) {
        super(0, 0, fraction, 1);
    }

}
