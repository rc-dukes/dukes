package nl.vaneijndhoven.geometry.pointinplane;

import nl.vaneijndhoven.geometry.Point;
import nl.vaneijndhoven.geometry.Point2D;
import nl.vaneijndhoven.geometry.Polygon;

/**
 * Created by jpoint on 16/08/16.
 */
public class CrossingNumber implements PointInPlane {
    @Override
    public boolean isPointInPlane(Point2D point, Polygon polygon) {
        return crossingNumber(point, polygon.getPoints().toArray(new Point2D[]{})) != 0;
    }

    // Copyright 2000 softSurfer, 2012 Dan Sunday
    // This code may be freely used and modified for any purpose
    // providing that this copyright notice is included with it.
    // SoftSurfer makes no warranty for this code, and cannot be held
    // liable for any real or imagined damage resulting from its use.
    // Users of this code must verify correctness for their application.

    /**
     * crossingNumber(): crossing number test for a point in a polygon
     *      Input:   P = a point,
     *               V[] = vertex points of a polygon V[n+1] with V[n]=V[0]
     *      Return:  0 = outside, 1 = inside
     * This code is patterned after [Franklin, 2000]
     */
    int crossingNumber(Point2D P, Point2D[] V) {
        int    cn = 0;    // the  crossing number counter

        // loop through all edges of the polygon
        for (int i=0; i<V.length; i++) {                            // edge from V[i]  to V[i+1]
            if (((V[i].getY() <= P.getY()) && (V[i+1].getY() > P.getY()))               // an upward crossing
                    || ((V[i].getY() > P.getY()) && (V[i+1].getY() <=  P.getY()))) {    // a downward crossing
                // compute  the actual edge-ray intersect x-coordinate
                double vt = (P.getY()  - V[i].getY()) / (V[i+1].getY() - V[i].getY());
                if (P.getX() <  V[i].getX() + vt * (V[i+1].getX() - V[i].getX()))       // P.getX() < intersect
                    ++cn;                                           // a valid crossing of y=P.getY() right of P.getX()
            }
        }
        return (cn&1);    // 0 if even (out), and 1 if  odd (in)
    }
}
