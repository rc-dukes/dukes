package nl.vaneijndhoven.objects.stoppingzone;

import nl.vaneijndhoven.objects.Boundary;
import nl.vaneijndhoven.opencv.linedetection.LineFilter;

public class ZoneEndBoundary extends Boundary {

    public static final double DEFAULT_ANGLE = 0;
    public static final double DEFAULT_TOLERANCE = 5;

    public ZoneEndBoundary() {
        this(DEFAULT_ANGLE, DEFAULT_TOLERANCE, false);
    }

    public ZoneEndBoundary(double angle, double margin, boolean directional) {
        super(new LineFilter(angle, margin, directional));
    }
}
