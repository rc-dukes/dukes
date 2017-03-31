package nl.vaneijndhoven.opencv.roi;

public class NearField extends RegionOfInterest {

    public NearField(double fraction) {
        super(0, 1 - fraction, 1, fraction);
    }

}