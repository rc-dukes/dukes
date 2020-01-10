package org.rcdukes.camera;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.rcdukes.geometry.Polygon;
import org.rcdukes.roi.ROI;

/**
 * test the perspective shift
 */
public class TestPerspectiveShift extends MatrixTestbase {
  String testPath = basePath + "target/test-classes/perspectiveshift/";

  @Test
  public void testChessboard() {
    CameraMatrix matrix = new CameraMatrix(7, 7);
    Mat chessboard = Imgcodecs.imread(testPath + "dukes_chessBoard008.png");
    Point[] corners = matrix.findOuterChessBoardCornerPoints(chessboard);
    assertNotNull(corners);
    assertEquals(4, corners.length);
    if (debug) {
      System.out.print("corners: ");
      String delim = "";
      for (int i = 0; i < 4; i++) {
        System.out.print(
            String.format("(%.1f,%.1f)%s", corners[i].x, corners[i].y, delim));
        delim = ",";
        if (i == 2)
          delim = "\n";
      }
    }
    writeImage("chessBoardCorners", chessboard);
    Size size = chessboard.size();
    Polygon imagePolygon = new ImagePolygon(size, 0, 0, 1, 0, 1, 1, 0, 1);
    Polygon worldPolygon = new ImagePolygon(corners);
    PerspectiveShift perspectiveShift = new PerspectiveShift(imagePolygon,
        worldPolygon);
    if (debug) {
      System.out.println("image: " + perspectiveShift.imagePoints);
      System.out.println("world: " + perspectiveShift.worldPoints);
    }
    Mat shifted = perspectiveShift.apply(chessboard);
    writeImage("shiftedChessBoard", shifted);
  }

  @Test
  public void testTransform() throws Exception {

    Mat original = Imgcodecs.imread(testPath + "dukes_livingroom.jpeg");
    ROI roi = new ROI("near", 0, 0.4, 1, 0.6);
    Mat image = roi.region(original);

    Polygon imagePolygon = new ImagePolygon(image.size(), 0, 0, 1, 0, 1, 1, 0,
        1);
    // double ry4s[] = { 0.40,0.41,0.42,0.43,0.44,0.45,0.46};
    // for (double ry4 : ry4s) {
    Polygon worldPolygon = new ImagePolygon(image.size(), 0.30, 0.20, 0.80,
        0.28, 0.96, 0.62, 0.15, 0.46);

    PerspectiveShift perspectiveShift = new PerspectiveShift(imagePolygon,
        worldPolygon);

    Mat shifted = perspectiveShift.apply(image);
    if (debug) {
      System.out.println("worldp:" + perspectiveShift.worldPolygon);
      System.out.println("world: " + perspectiveShift.worldPoints);
      writeImage("roiImage", image);
      writeImage("shiftedImage", shifted);
    }
    // }
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