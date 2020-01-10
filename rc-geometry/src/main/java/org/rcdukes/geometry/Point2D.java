package org.rcdukes.geometry;

/**
 * represents a 2 dimensional point
 *
 */
public interface Point2D {

  /**
   * getter for x coordinate
   * 
   * @return x coordinate
   */
  double getX();

  /**
   * getter for y coordinate
   * @return y coordinate
   */
  double getY();

  /**
   * calculate the distance to another point
   * @param other
   * @return the distance
   */
  double distance(Point2D other);

}
