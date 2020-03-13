package org.rcdukes.objects.stoppingzone;

import org.rcdukes.geometry.Lane;
import org.rcdukes.geometry.Line;
import org.rcdukes.geometry.Point;
import org.rcdukes.geometry.Point2D;
import org.rcdukes.objects.StoppingZone;
import org.rcdukes.objects.ViewPort;

import java.util.Optional;

public class StoppingZoneOrientation {

    private final StoppingZone stoppingZone;
    private final Lane lane;
    private final ViewPort viewPort;

    public StoppingZoneOrientation(StoppingZone stoppingZone, Lane lane, ViewPort viewPort) {
        this.stoppingZone = stoppingZone;
        this.lane = lane;
        this.viewPort = viewPort;
    }

    public double determineDistanceToStoppingZone() {
        if (!stoppingZone.getEntrance().isPresent()) {
            return Double.NaN;
        }

        Optional<Point2D> leftIntersect = lane.getLeftBoundary().flatMap(boundary -> stoppingZone.getEntrance().get().intersect(boundary));
        Optional<Point2D> rightIntersect = lane.getRightBoundary().flatMap(boundary -> stoppingZone.getEntrance().get().intersect(boundary));

        if (!leftIntersect.isPresent() || !rightIntersect.isPresent()) {
            return Double.NaN;
        }

        Line entrance = new Line(leftIntersect.get(), rightIntersect.get());

        Point2D base = new Point(viewPort.getOrigin().getX() + (viewPort.getWidth() / 2), viewPort.getOrigin().getY() + viewPort.getHeight());
        return entrance.distance(base);
    }

    public double determineDistanceToStoppingZoneEnd() {
        if (!stoppingZone.getExit().isPresent()) {
            return Double.NaN;
        }

        Optional<Point2D> leftIntersect = lane.getLeftBoundary().flatMap(boundary -> stoppingZone.getExit().get().intersect(boundary));
        Optional<Point2D> rightIntersect = lane.getRightBoundary().flatMap(boundary -> stoppingZone.getExit().get().intersect(boundary));

        if (!leftIntersect.isPresent() || !rightIntersect.isPresent()) {
            return Double.NaN;
        }

        Line exit = new Line(leftIntersect.get(), rightIntersect.get());

        Point2D base = new Point(viewPort.getOrigin().getX() + (viewPort.getWidth() / 2), viewPort.getOrigin().getY() + viewPort.getHeight());
        return exit.distance(base);
    }
}