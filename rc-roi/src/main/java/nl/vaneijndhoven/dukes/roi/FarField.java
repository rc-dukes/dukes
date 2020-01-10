package nl.vaneijndhoven.dukes.roi;

/**
 *  full width fractional height Region of Interest
 *  filtering top fraction of image
 *
 */
public class FarField extends ROI {

    /**
     * get a full width field of view with the given fractional height
     * @param fraction - the fractional part of the image
     */
    public FarField(double fraction) {
        super(0, 0, 1, fraction);
    }

}
