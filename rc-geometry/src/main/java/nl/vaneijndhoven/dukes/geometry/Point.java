package nl.vaneijndhoven.dukes.geometry;

import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * implementation of point with multiple dimensions
 * with 2D and 3D interfaces
 *
 */
public class Point implements Point2D, Point3D, Comparable<Point> {

    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;

    double[] dimensions;

    /**
     * create a point with the given dimensions
     * @param dimensions - the dimensions to use
     */
    public Point(double... dimensions) {
        if (dimensions.length < 1) {
            throw new IllegalArgumentException("Point must have at least 1 dimension");
        }

        this.dimensions = dimensions;
    }

    /**
     * calculate the distance to another point
     * @param dimensions - the dimensions of the other point
     * @return - the distance
     */
    public double distance(double ... dimensions) {
        if (this.dimensions.length != dimensions.length) {
            throw new IllegalArgumentException("Can only calculate distance between two points with similar dimensions.");
        }

        double totalDistance = 0d;

        for (int i = 0; i < this.dimensions.length; i++) {
            double myVal = this.dimensions[i];
            double otherVal = dimensions[i];

            totalDistance += Math.pow(myVal - otherVal, 2d);
        }

        return Math.sqrt(totalDistance);
    }

    @Override
    public double distance(Point2D other) {
        double[] otherDimensons = new double[2];
        otherDimensons[X] = other.getX();
        otherDimensons[Y] = other.getY();
        return distance(otherDimensons);
    }

    @Override
    public double distance(Point3D other) {
        double[] otherDimensons = new double[3];
        otherDimensons[X] = other.getX();
        otherDimensons[Y] = other.getY();
        otherDimensons[Z] = other.getZ();
        return distance(otherDimensons);
    }

    @Override
    public String toString() {
        return "{" + stream(dimensions).mapToObj(String::valueOf).collect(Collectors.joining(",")) + "}";
    }

    @Override
    public double getX() {
        return dimensions[X];
    }

    @Override
    public double getY() {
        return dimensions[Y];
    }

    @Override
    public double getZ() {
        return dimensions[Z];
    }
    
    private Double getMax() {
     Double max=Double.MIN_VALUE;
     for (double val:dimensions) {
       if (val>max)
         max=val;
     }
     return max;
    }

    @Override
    public int compareTo(Point o) {
      Double max=getMax();
      Double omax=o.getMax();
      return max.compareTo(omax);
    }


}