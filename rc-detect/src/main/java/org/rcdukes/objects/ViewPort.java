package org.rcdukes.objects;

import org.rcdukes.geometry.Point2D;

/**
 * a viewport
 *
 */
public class ViewPort {

    private Point2D origin;
    private double width;
    private double height;


    /**
     * construct me
     * @param origin
     * @param width
     * @param height
     */
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
