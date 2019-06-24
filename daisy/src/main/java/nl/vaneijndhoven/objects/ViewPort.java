package nl.vaneijndhoven.objects;

import nl.vaneijndhoven.dukes.geometry.Point2D;

public class ViewPort {

    private Point2D origin;
    private double width;
    private double height;


    public ViewPort(Point2D origin, double width, double height) {
        this.origin = origin;
        this.width = width;
        this.height = height;
    }

    public Point2D getOrigin() {
        return origin;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
