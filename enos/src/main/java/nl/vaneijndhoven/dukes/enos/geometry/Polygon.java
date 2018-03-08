package nl.vaneijndhoven.dukes.enos.geometry;

import nl.vaneijndhoven.dukes.enos.geometry.pointinplane.CrossingNumber;
import nl.vaneijndhoven.dukes.enos.geometry.pointinplane.PointInPlane;
//import org.opencv.core.Point2D;

import java.util.*;

public class Polygon {

    private final List<Point2D> points;

    private PointInPlane pointInPlaneStrategy = new CrossingNumber();

    public Polygon(Point2D... points) {
        this.points = Arrays.asList(points);
    }

    public List<Point2D> getPoints() {
        return points;
    }

    public List<Point2D> getPointsClockwise() {
        List<Point2D> copy = new ArrayList<>(points);

        if (shoelace() < 0) {
            Collections.reverse(copy);
        }

        return copy;
    }

    public List<Point2D> getPointsCounterClockwise() {
        List<Point2D> copy = new ArrayList<>(points);

        if (shoelace() >= 0) {
            Collections.reverse(copy);
        }

        return copy;
    }

    public static Polygon square(Point2D origin, Point2D opposite) {
        Point2D point1 = origin;
        Point2D point2 = new Point(opposite.getX(), origin.getY());
        Point2D point3 = opposite;
        Point2D point4 = new Point(origin.getX(), opposite.getY());

        return new Polygon(point1, point2, point3, point4);
    }

    public Set<Point2D> intersect(Line line) {
        Set<Point2D> intersections = new HashSet<>();

        for (int i = 0; i < points.size(); i++) {
            Point2D point1 = points.get(i);
            Point2D point2 = points.get(i == points.size() - 1 ? 0 : i + 1);
            Line boundary = new Line(point1, point2);
            boundary.intersect(line)
                    .filter(boundary::existsOnLine)
                    .ifPresent(intersections::add);
        }

        System.out.println("Intersections found at: " + intersections);
        return intersections;
    }

    public List<Line> edges() {
        List<Line> edges = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            Point2D point1 = points.get(i);
            Point2D point2 = points.get(i == points.size() - 1 ? 0 : i + 1);
            edges.add(new Line(point1, point2));

        }

        return edges;
    }

    public void setPointInPlaneStrategy(PointInPlane pointInPlaneStrategy) {
        this.pointInPlaneStrategy = pointInPlaneStrategy;
    }

    /**
     * Calculate polygon surface using shoelace method.
     * @return
     */
    private double shoelace() {
        Point2D previous = points.get(points.size() - 1);

        double size = 0;
        for (Point2D current : points) {
            size += (previous.getX() - current.getX()) * (previous.getY() + current.getY());
            previous = current;
        }

        return size;
    }
}
