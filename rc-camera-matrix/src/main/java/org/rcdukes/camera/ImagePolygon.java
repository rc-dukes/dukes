package org.rcdukes.camera;

import org.opencv.core.Size;

import nl.vaneijndhoven.dukes.geometry.Point;
import nl.vaneijndhoven.dukes.geometry.Polygon;

public class ImagePolygon extends Polygon {

  /**
   * construct me from relative values
   * 
   * @param size
   * @param rx1
   * @param ry1
   * @param rx2
   * @param ry2
   * @param rx3
   * @param ry3
   * @param rx4
   * @param ry4
   */
  public ImagePolygon(Size size, double rx1, double ry1, double rx2, double ry2,
      double rx3, double ry3, double rx4, double ry4) {
    super(new Point(rx1 * size.width, ry1 * size.height),
        new Point(rx2 * size.width, ry2 * size.height),
        new Point(rx3 * size.width, rx3 * size.height),
        new Point(rx4 * size.width, ry4 * size.height));
  }
}
