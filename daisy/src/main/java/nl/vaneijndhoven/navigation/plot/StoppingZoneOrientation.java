package nl.vaneijndhoven.navigation.plot;

import nl.vaneijndhoven.dukes.geometry.Line;
import nl.vaneijndhoven.dukes.geometry.Point;
import nl.vaneijndhoven.dukes.geometry.Point2D;
import nl.vaneijndhoven.objects.Lane;
import nl.vaneijndhoven.objects.StoppingZone;
import nl.vaneijndhoven.objects.ViewPort;

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