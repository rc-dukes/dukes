package nl.vaneijndhoven.dukes.enos.geometry.pointinplane;

import nl.vaneijndhoven.dukes.enos.geometry.Line;
import nl.vaneijndhoven.dukes.enos.geometry.Point2D;
import nl.vaneijndhoven.dukes.enos.geometry.Polygon;

import java.util.ArrayList;

/**
 * Created by jpoint on 16/08/16.
 */
public class WindingNumbers implements PointInPlane {

    @Override
    public boolean isPointInPlane(Point2D point, Polygon polygon) {
        ArrayList<Point2D> copy = new ArrayList(polygon.getPoints());
        copy.add(copy.get(0));
        return windingNumber(point, copy.toArray(new Point2D[]{})) != 0;
    }

    // Copyright 2000 softSurfer, 2012 Dan Sunday
    // This code may be freely used and modified for any purpose
    // providing that this copyright notice is included with it.
    // SoftSurfer makes no warranty for this code, and cannot be held
    // liable for any real or imagined damage resulting from its use.
    // Users of this code must verify correctness for their application.

    /**
     * isLeft(): tests if a point is Left|On|Right of an infinite line.
     *    Input:  three points P0, P1, and P2
     *    Return: >0 for P2 left of the line through P0 and P1
     *            =0 for P2  on the line
     *            <0 for P2  right of the line
     *    See: Algorithm 1 "Area of Triangles and Polygons"
     */
    int howLeft( Point2D P0, Point2D P1, Point2D P2 ) {
        return (int)((P1.getX() - P0.getX()) * (P2.getY() - P0.getY())
                - (P2.getX() -  P0.getX()) * (P1.getY() - P0.getY()) );
    }

    boolean isLeft( Point2D P0, Point2D P1, Point2D P2 ) {
        return howLeft(P0, P1, P2) > 0;
    }

    boolean isRight( Point2D P0, Point2D P1, Point2D P2 ) {
        return howLeft(P0, P1, P2) < 0;
    }
    //===================================================================

    //===================================================================


    /**
     * windingNumber(): winding number test for a point in a polygon
     *      Input:   point = a point,
     *               vertex[] = vertex points of a polygon vertex[n+1] with vertex[n]=vertex[0]
     *      Return:  wn = the winding number (=0 only when point is outside)
     */
    int windingNumber(Point2D point, Point2D[] vertex)
    {
        int    wn = 0;    // the  winding number counter

        // loop through all edges of the polygon
        for (int i=0; i<vertex.length-1; i++) {   // edge from vertex[i] to  vertex[i+1]
            Point2D currentVertexPoint = vertex[i];
            Point2D nextVertexPoint = vertex[i + 1];
            Line line = new Line(currentVertexPoint, nextVertexPoint);
            if (isAboveOrEqual(point, currentVertexPoint)) {          // start y <= point.getY()
                if (isBelow(point, nextVertexPoint))      // an upward crossing
                    if (line.isLeftOfLine(point))  // point left of  edge
                        ++wn;            // have  a valid up intersect
            } else {                        // start y > point.getY() (no test needed)
                if (isAboveOrEqual(point, nextVertexPoint))     // a downward crossing
                    if (line.isRightOfLine(point))  // point right of  edge
                        --wn;            // have  a valid down intersect
            }
        }
        return wn;
    }

    private boolean isBelow(Point2D point, Point2D nextVertexPoint) {
        return nextVertexPoint.getY()  > point.getY();
    }

    private boolean isAboveOrEqual(Point2D point, Point2D reference) {
        return reference.getY() <= point.getY();
    }
}
