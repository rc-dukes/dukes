package nl.vaneijndhoven.objects;

import nl.vaneijndhoven.opencv.linedetection.LineFilter;
import nl.vaneijndhoven.geometry.Line;

import java.util.Collection;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Boundary {

    private LineFilter filter;

    public Boundary(LineFilter filter) {
        this.filter = filter;
    }

    public Optional<Line> boundary(Collection<Line> lines) {
        return ofNullable(Line.average(candidates(lines)));
    }

    public Collection<Line> candidates(Collection<Line> lines) {
        return filter.filter(lines);
    }

}
