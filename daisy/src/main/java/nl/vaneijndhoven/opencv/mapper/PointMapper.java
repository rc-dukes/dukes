package nl.vaneijndhoven.opencv.mapper;

import org.opencv.core.Point;

public class PointMapper {

    public static Point toPoint(nl.vaneijndhoven.geometry.Point2D point) {
        return new Point(point.getX(), point.getY());
    }

}
