package nl.vaneijndhoven.objects.stoppingzone;

import org.rcdukes.detect.linedetection.LineFilter;
import org.rcdukes.geometry.Line;
import nl.vaneijndhoven.objects.Boundary;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

public class ZoneStartBoundary extends Boundary {

    public static final double DEFAULT_ANGLE = 0;
    public static final double DEFAULT_TOLERANCE = 5;

    public ZoneStartBoundary() {
        this(DEFAULT_ANGLE, DEFAULT_TOLERANCE, false);
    }

    public ZoneStartBoundary(double angle, double margin, boolean directional) {
        super(new LineFilter(angle, margin, directional));
    }

    public Optional<Line> boundary(Collection<Line> lines) {
        Comparator<Line> lineComparator = (line1, line2) -> Double.compare(line1.bottomMost().getY(), line2.bottomMost().getY());
        lineComparator.reversed();
        Optional<Line> nearest = candidates(lines).stream().sorted(lineComparator.reversed()).findFirst();
        return nearest;
    }
}
