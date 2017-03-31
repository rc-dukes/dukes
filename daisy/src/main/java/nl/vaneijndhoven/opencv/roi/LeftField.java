package nl.vaneijndhoven.opencv.roi;

import org.opencv.core.Rect;
import org.opencv.core.Size;

public class LeftField extends RegionOfInterest {

    public LeftField(double fraction) {
        super(0, 0, fraction, 1);
    }

}
