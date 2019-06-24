package nl.vaneijndhoven.dukes.geometry;

public interface Point3D extends Point2D {

    double getZ();

    double distance(Point3D other);
}
