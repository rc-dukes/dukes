package org.rcdukes.geometry.pointinplane;

import org.rcdukes.geometry.Point2D;
import org.rcdukes.geometry.Polygon;

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
