package nl.vaneijndhoven.objects.stoppingzone;

import org.rcdukes.detect.linedetection.LineFilter;

import nl.vaneijndhoven.objects.Boundary;

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
