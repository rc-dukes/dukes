package org.rcdukes.camera;

import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.rcdukes.geometry.Point;
import org.rcdukes.geometry.Polygon;
import org.rcdukes.roi.ROI;

/**
 * a polygon defined from an OpenCV image
 * 
 * @author wf
 *
 */
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
    double w = size.width;
    double h = size.height;
    init(
        new Point(rx1 * w, ry1 * h), 
        new Point(rx2 * w, ry2 * h),
        new Point(rx3 * w, ry3 * h), 
        new Point(rx4 * w, ry4 * h)
    );
  }

  /**
   * construct me from the given  corner points
   * @param corners
   *          - corner points
   */
  public ImagePolygon(org.opencv.core.Point[] corners) {
    init(
        new Point(corners[0].x, corners[0].y),
        new Point(corners[1].x, corners[1].y),
        new Point(corners[2].x, corners[2].y),
        new Point(corners[3].x, corners[3].y)
    );
  }

  /**
   * create an ImagePolygon for the given roi
   * @param size
   * @param roi
   */
  public ImagePolygon(Size size, ROI roi) {
    Rect r = roi.roiRect(size);
    init(
        new Point(r.x,r.y),
        new Point(r.x+r.width,r.y),
        new Point(r.x+r.width,r.y+r.height),
        new Point(r.x,r.y+r.height)
    );
  }
}
