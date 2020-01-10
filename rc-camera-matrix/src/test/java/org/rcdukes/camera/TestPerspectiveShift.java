package org.rcdukes.camera;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

import nl.vaneijndhoven.dukes.geometry.Polygon;
import nl.vaneijndhoven.dukes.roi.ROI;

/**
 * test the perspective shift
 */
public class TestPerspectiveShift extends MatrixTestbase {
  String testPath = basePath + "target/test-classes/perspectiveshift/";

  @Test
  public void testChessboard() {
    CameraMatrix matrix = new CameraMatrix(7, 7);
    Mat chessboard = Imgcodecs.imread(testPath + "chessBoard008.png");
    Point[] corners = matrix.findOuterChessBoardCornerPoints(chessboard);
    assertNotNull(corners);
    assertEquals(4,corners.length);
    writeImage("chessBoardCorners", chessboard);
  }

  @Test
  public void transform() throws Exception {

    Mat original = Imgcodecs.imread(testPath + "chessBoard008.png");
    // relative width and height
    double rw = 0.55;
    double rh = 0.45;
    Mat image = new ROI(0, rw, 1, rh).region(original);

    Polygon imagePolygon = new ImagePolygon(image.size(), 0, 0, 1, 0, 1, 1, 0,
        1);
    Polygon worldPolygon = new ImagePolygon(image.size(), 0, 0, rw, 0, rw, rh,
        0, rh);

    PerspectiveShift perspectiveShift = new PerspectiveShift(imagePolygon,
        worldPolygon);

    Mat shifted = perspectiveShift.apply(image);
    if (debug) {
      writeImage("roiImage", image);
      writeImage("shiftedImag", shifted);
    }
  }

  /**
   * write the given image
   * 
   * @param name
   * @param image
   */
  public void writeImage(String name, Mat image) {
    Imgcodecs.imwrite(testPath + name + ".jpg", image);
  }
}