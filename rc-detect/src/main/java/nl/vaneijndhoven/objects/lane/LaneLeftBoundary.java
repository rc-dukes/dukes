package nl.vaneijndhoven.objects.lane;

import org.rcdukes.detect.linedetection.LineFilter;
import org.rcdukes.geometry.Line;
import nl.vaneijndhoven.objects.Boundary;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

public class LaneLeftBoundary extends Boundary {

    public static final double DEFAULT_ANGLE = -BoundaryDefaults.DEFAULT_LANE_BOUNDARY_ANGLE;
    public static final double DEFAULT_TOLERANCE = BoundaryDefaults.DEFAULT_LANE_BOUNDARY_TOLERANCE;

    public LaneLeftBoundary() {
        this(DEFAULT_ANGLE, DEFAULT_TOLERANCE, false);
    }

    public LaneLeftBoundary(double angle, double margin, boolean directional) {
        super(new LineFilter(angle, margin, directional));
    }

    public Optional<Line> boundary(Collection<Line> lines) {
        Comparator<Line> yComparator = (line1, line2) -> Double.compare(line1.bottomMost().getY(), line2.bottomMost().getY());
        Comparator<Line> xComparator = (line1, line2) -> Double.compare(line1.bottomMost().getX(), line2.bottomMost().getX());
        Comparator<Line> lineComparator = yComparator.thenComparing(xComparator);
        Optional<Line> nearest = candidates(lines).stream().sorted(lineComparator).findFirst();
        return nearest;
//        return Line.average(candidates(lines));
    }
}