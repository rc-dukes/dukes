package org.rcdukes.detect.lanedetection;

import java.util.Collection;
import java.util.Optional;

import org.rcdukes.detectors.LaneDetector;
import org.rcdukes.geometry.Lane;
import org.rcdukes.geometry.Line;
import org.rcdukes.geometry.Point;
import org.rcdukes.geometry.Point2D;

import nl.vaneijndhoven.objects.ViewPort;
import nl.vaneijndhoven.objects.lane.LaneLeftBoundary;
import nl.vaneijndhoven.objects.lane.LaneRightBoundary;

public class DefaultLaneDetector implements LaneDetector {

    @Override
    public Lane detect(Collection<Line> lines, ViewPort viewPort) {
        Optional<Line> leftLine = new LaneLeftBoundary().boundary(lines);
        Optional<Line> rightLine = new LaneRightBoundary().boundary(lines);

        final Line bottom = new Line(new Point(0d, viewPort.getHeight()), new Point(viewPort.getWidth(), viewPort.getHeight()));
        final Line horizon = new Line(new Point(0d, 0d), new Point((double) viewPort.getWidth(), 0d));

        Optional<Line> leftBoundary = leftLine.map(line -> stretch(line, bottom, horizon));
        Optional<Line> rightBoundary = rightLine.map(line -> stretch(line, bottom, horizon));

        Lane lane = new Lane(leftBoundary, rightBoundary);

        return lane;
    }

    private Line stretch(Line line, Line bottom, Line horizon) {
        Point2D lineTop = horizon.intersect(line).get();
        Point2D lineBottom = bottom.intersect(line).get();
        return new Line(lineBottom, lineTop);
    }
}
