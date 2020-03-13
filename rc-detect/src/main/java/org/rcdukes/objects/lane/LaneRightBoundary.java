package org.rcdukes.objects.lane;

import org.rcdukes.detect.linedetection.LineFilter;
import org.rcdukes.geometry.Line;
import org.rcdukes.objects.Boundary;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

public class LaneRightBoundary extends Boundary {

    public static final double DEFAULT_ANGLE = BoundaryDefaults.DEFAULT_LANE_BOUNDARY_ANGLE;
    public static final double DEFAULT_TOLERANCE = BoundaryDefaults.DEFAULT_LANE_BOUNDARY_TOLERANCE;

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