package nl.vaneijndhoven.opencv.mapper;

import org.opencv.core.Point;

import nl.vaneijndhoven.dukes.geometry.Point2D;

public class PointMapper {

    public static Point toPoint(Point2D point) {
        return new Point(point.getX(), point.getY());
    }

}
