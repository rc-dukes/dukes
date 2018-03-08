package nl.vaneijndhoven.opencv.mapper;

import nl.vaneijndhoven.dukes.enos.geometry.Point2D;
import org.opencv.core.Point;

public class PointMapper {

    public static Point toPoint(Point2D point) {
        return new Point(point.getX(), point.getY());
    }

}
