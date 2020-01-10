package org.rcdukes.video;

import org.opencv.core.Point;

import org.rcdukes.geometry.Point2D;

/**
 * map an rc dukes geometry point to an openCV point
 *
 */
public class PointMapper {

  /**
   * convert the given rc dukes geometry 2D point to an openCV point
   * @param point - the 2D Point
   * @return - the corresponding openCV point
   */
  public static Point toPoint(Point2D point) {
    return new Point(point.getX(), point.getY());
  }

}
