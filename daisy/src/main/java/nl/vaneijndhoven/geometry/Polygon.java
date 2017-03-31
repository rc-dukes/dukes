package nl.vaneijndhoven.geometry;

import nl.vaneijndhoven.geometry.pointinplane.CrossingNumber;
import nl.vaneijndhoven.geometry.pointinplane.PointInPlane;
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
}
