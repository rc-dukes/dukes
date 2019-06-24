package nl.vaneijndhoven.dukes.geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * implements a polygon as a list of Point2D
 *
 */
public class Polygon {

    public static boolean debug=false;
    private final List<Point2D> points;

    // private PointInPlane pointInPlaneStrategy = new CrossingNumber();

    /**
     * create a Polygon from the given points
     * @param points
     */
    public Polygon(Point2D... points) {
        this.points = Arrays.asList(points);
    }

    /**
     * getter for the points of this polygon
     * @return - the points
     */
    public List<Point2D> getPoints() {
        return points;
    }

    /**
     * get the points of the polygon in clockwise manner
     * @return the list of points
     */
    public List<Point2D> getPointsClockwise() {
        List<Point2D> copy = new ArrayList<>(points);

        if (shoelace() < 0) {
            Collections.reverse(copy);
        }

        return copy;
    }

    /**
     * get the points of the polygon in counter-clockwise manner
     * @return the list of points
     */
    public List<Point2D> getPointsCounterClockwise() {
        List<Point2D> copy = new ArrayList<>(points);

        if (shoelace() >= 0) {
            Collections.reverse(copy);
        }

        return copy;
    }

    /**
     * create a square from the given two points
     * @param origin - first point
     * @param opposite - second point
     * @return - the polygon representing the square
     */
    public static Polygon square(Point2D origin, Point2D opposite) {
        Point2D point1 = origin;
        Point2D point2 = new Point(opposite.getX(), origin.getY());
        Point2D point3 = opposite;
        Point2D point4 = new Point(origin.getX(), opposite.getY());

        return new Polygon(point1, point2, point3, point4);
    }

    /**
     * get the list of points where the given line intersects with the polygon
     * @param line - the line to test
     * @return - the intersection point
     */
    public Set<Point2D> intersect(Line line) {
        Set<Point2D> intersections = new TreeSet<>();

        for (int i = 0; i < points.size(); i++) {
            Point2D point1 = points.get(i);
            Point2D point2 = points.get(i == points.size() - 1 ? 0 : i + 1);
            Line boundary = new Line(point1, point2);
            boundary.intersect(line)
                    .filter(boundary::existsOnLine)
                    .ifPresent(intersections::add);
        }

        if (debug)
          System.out.println("Intersections found at: " + intersections);
        return intersections;
    }

    /**
     * get the edges of the polygon
     * @return - the lines for the edges
     */
    public List<Line> edges() {
        List<Line> edges = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            Point2D point1 = points.get(i);
            Point2D point2 = points.get(i == points.size() - 1 ? 0 : i + 1);
            edges.add(new Line(point1, point2));
        }
        return edges;
    }

    /*
    public void setPointInPlaneStrategy(PointInPlane pointInPlaneStrategy) {
        this.pointInPlaneStrategy = pointInPlaneStrategy;
    }*/

    /**
     * Calculate polygon surface using shoe lace method.
     * @see <a href='https://en.wikipedia.org/wiki/Shoelace_formula'>Shoelace formula on Wikipedia</a>
     * @return the surface
     */
    public double shoelace() {
        Point2D previous = points.get(points.size() - 1);

        double size = 0;
        for (Point2D current : points) {
            size += (previous.getX() - current.getX()) * (previous.getY() + current.getY());
            previous = current;
        }

        return size;
    }
}
