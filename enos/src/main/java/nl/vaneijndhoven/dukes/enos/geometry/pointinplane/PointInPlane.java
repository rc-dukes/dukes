package nl.vaneijndhoven.dukes.enos.geometry.pointinplane;

import nl.vaneijndhoven.dukes.enos.geometry.Point2D;
import nl.vaneijndhoven.dukes.enos.geometry.Polygon;

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
