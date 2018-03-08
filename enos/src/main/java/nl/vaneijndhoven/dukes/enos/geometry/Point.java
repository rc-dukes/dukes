package nl.vaneijndhoven.dukes.enos.geometry;

import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class Point implements Point2D, Point3D {

    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;

    double[] dimensions;

    private Point(int dimensions) {
        this.dimensions = new double[dimensions];
    }

    public Point(double... dimensions) {
        if (dimensions.length < 1) {
            throw new IllegalArgumentException("Point must have at least 1 dimension");
        }

        this.dimensions = dimensions;
    }

    public double distance(double[] dimensions) {
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

//    public static class Point2D extends Point {
//
//        public static final int X = 0;
//        public static final int Y = 1;
//
//        public Point2D(double x, double y) {
//            this(x, y, 2);
//        }
//        public Point2D(double x, double y, int dimensions) {
//            super(dimensions);
//            this.dimensions[X] = x;
//            this.dimensions[Y] = y;
//        }
//
//        public double getX() {
//            return dimensions[X];
//        }
//
//        public double getY() {
//            return dimensions[Y];
//        }
//    }
//
//    public static class Point3D extends Point2D {
//
//        public static final int Z = 2;
//
//        public Point3D(double x, double y, double z) {
//            super(x, y, 3);
//            this.dimensions[Z] = z;
//        }
//
//        public double getZ() {
//            return dimensions[Z];
//        }
//    }
}