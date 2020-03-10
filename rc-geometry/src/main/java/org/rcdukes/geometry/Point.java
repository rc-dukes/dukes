package org.rcdukes.geometry;

/**
 * implementation of point with multiple dimensions with 2D  interfaces
 *
 */
public class Point implements Point2D, Comparable<Point> {

  public static final int X = 0;
  public static final int Y = 1;
  public static final int Z = 2;

  Double[] dimensions = { null, null, null };

  public Point() {
  }
  
  /**
   * convenience constructor
   * @param x
   * @param y
   */
  public Point(int x,int y) {
    this(new Double(x),new Double(y));
  }
  
  /**
   * convenience constructor
   * @param x
   * @param y
   */
  public Point(long x,long y) {
    this(new Double(x),new Double(y));
  }

  /**
   * create a point with the given dimensions
   * 
   * @param dimensions
   *          - the dimensions to use
   */
  public Point(Double... dimensions) {
    if (dimensions.length < 1) {
      throw new IllegalArgumentException(
          "Point must have at least 1 dimension");
    }

    this.dimensions = dimensions;
  }

  /**
   * calculate the distance to another point
   * 
   * @param otherDimensons
   *          - the dimensions of the other point
   * @return - the distance
   */
  public double distance(Double... otherDimensons) {
    if (this.dimensions.length != otherDimensons.length) {
      throw new IllegalArgumentException(
          "Can only calculate distance between two points with similar dimensions.");
    }

    double totalDistance = 0d;

    for (int i = 0; i < this.dimensions.length; i++) {
      double myVal = this.dimensions[i];
      double otherVal = otherDimensons[i];

      totalDistance += Math.pow(myVal - otherVal, 2d);
    }

    return Math.sqrt(totalDistance);
  }

  @Override
  public double distance(Point2D other) {
    Double[] otherDimensons = new Double[2];
    otherDimensons[X] = other.getX();
    otherDimensons[Y] = other.getY();
    return distance(otherDimensons);
  }

  public String valueStr(Double d) {
    String text = d == null ? "?" : "" + d;
    return text;
  }

  @Override
  public String toString() {
    String text = "?";
    switch (dimensions.length) {
    case 2:
      text = String.format("{%s,%s}", valueStr(getX()), valueStr(getY()));
      break;
    }
    return text;
  }

  @Override
  public double getX() {
    return dimensions[X];
  }
  
  public void setX(double x) {
    dimensions[X]=x;
  }

  @Override
  public double getY() {
    return dimensions[Y];
  }
  
  public void setY(double y) {
    dimensions[Y]=y;
  }
  
  public void setZ(Double z) {
    dimensions[Z]=z;
  }

  private Double getMax() {
    Double max = Double.MIN_VALUE;
    for (double val : dimensions) {
      if (val > max)
        max = val;
    }
    return max;
  }

  @Override
  public int compareTo(Point o) {
    Double max = getMax();
    Double omax = o.getMax();
    return max.compareTo(omax);
  }

}