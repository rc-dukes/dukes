package nl.vaneijndhoven.geometry.pointinplane;

import nl.vaneijndhoven.geometry.Point;
import nl.vaneijndhoven.geometry.Point2D;
import nl.vaneijndhoven.geometry.Polygon;

/**
 * Created by jpoint on 16/08/16.
 */
public interface PointInPlane {

//    enum State {
//        IN,
//        OUT
//    }

    boolean isPointInPlane(Point2D point, Polygon polygon);
}
