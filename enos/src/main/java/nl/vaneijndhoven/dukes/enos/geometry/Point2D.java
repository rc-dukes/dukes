package nl.vaneijndhoven.dukes.enos.geometry;

public interface Point2D {

    double getX();
    double getY();

    double distance(Point2D other);

}
