package nl.vaneijndhoven.objects.lane;

import nl.vaneijndhoven.dukes.geometry.Line;
import nl.vaneijndhoven.objects.Boundary;
import nl.vaneijndhoven.opencv.linedetection.LineFilter;
import nl.vaneijndhoven.opencv.video.LaneDetectionController;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

public class LaneRightBoundary extends Boundary {

    public static final double DEFAULT_ANGLE = LaneDetectionController.DEFAULT_LANE_BOUNDARY_ANGLE;
    public static final double DEFAULT_TOLERANCE = LaneDetectionController.DEFAULT_LANE_BOUNDARY_TOLERANCE;

    public LaneRightBoundary() {
        this(DEFAULT_ANGLE, DEFAULT_TOLERANCE, false);
    }

    public LaneRightBoundary(double angle, double margin, boolean directional) {
        super(new LineFilter(angle, margin, directional));
    }

    public Optional<Line> boundary(Collection<Line> lines) {
        Comparator<Line> yComparator = (line1, line2) -> Double.compare(line1.bottomMost().getY(), line2.bottomMost().getY());
        Comparator<Line> xComparator = (line1, line2) -> Double.compare(line1.bottomMost().getX(), line2.bottomMost().getX());
        Comparator<Line> lineComparator = yComparator.thenComparing(xComparator.reversed());
        Optional<Line> nearest = candidates(lines).stream().sorted(lineComparator).findFirst();
        return nearest;
    }
}