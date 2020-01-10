package org.rcdukes.geometry;

/**
 * represents a 3 dimensional point
 */
public interface Point3D extends Point2D {

    double getZ();

    double distance(Point3D other);
}
