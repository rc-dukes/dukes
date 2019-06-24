package nl.vaneijndhoven.dukes.geometry.pointinplane;

import nl.vaneijndhoven.dukes.geometry.Point2D;
import nl.vaneijndhoven.dukes.geometry.Polygon;

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
