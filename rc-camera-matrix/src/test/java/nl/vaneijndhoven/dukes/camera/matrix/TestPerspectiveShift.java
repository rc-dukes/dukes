package nl.vaneijndhoven.dukes.camera.matrix;

import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

import com.bitplan.opencv.NativeLibrary;

import nl.vaneijndhoven.dukes.camera.matrix.PerspectiveShift;
import nl.vaneijndhoven.dukes.geometry.Point;
import nl.vaneijndhoven.dukes.geometry.Polygon;
import nl.vaneijndhoven.dukes.roi.RegionOfInterest;

/**
 * test the perspective shift
 */
public class TestPerspectiveShift extends MatrixTestbase {

  @Test
  public void transform() throws Exception {
    //if (!isTravis()) {
      NativeLibrary.load();
      // CameraMatrix matrix = new CameraMatrix(8, 6);

      // ArrayList<Mat> images = new ArrayList<>();
      // Files.newDirectoryStream(Paths.get(basePath+"target/test-classes/perspectiveshift"),
      // path ->
      // path.getFileName().toString().startsWith("GOPR"))
      // .forEach(path -> {
      // System.out.println("reading: " + path);
      // String substring = path.toString();
      // Mat image = Imgcodecs.imread(substring);
      // matrix.calibrate(image);
      // });

      // images.forEach(Mat::release);

      Mat original = Imgcodecs.imread(
          basePath + "target/test-classes/perspectiveshift/screen_shot.png");
      Mat image = new RegionOfInterest(0, 0.55, 1, 0.45).region(original);

      Size imageSize = image.size();

      Polygon imagePolygon = new Polygon(
          new Point(0 * imageSize.width, imageSize.height),
          new Point(1 * imageSize.width, imageSize.height),
          new Point(0 * imageSize.width, 0 * imageSize.height),
          new Point(1 * imageSize.width, 0 * imageSize.height));

      Polygon worldPolygon = new Polygon(
          new Point(0.45 * imageSize.width, imageSize.height),
          new Point(0.55 * imageSize.width, imageSize.height),
          new Point(0 * imageSize.width, 0 * imageSize.height),
          new Point(1 * imageSize.width, 0 * imageSize.height));

      PerspectiveShift perspectiveShift = new PerspectiveShift(imagePolygon,
          worldPolygon);

      Mat shifted = perspectiveShift.apply(image);
      if (debug) {
        Imgcodecs.imwrite(
            basePath + "target/test-classes/perspectiveshift/debug.jpg", image);
        Imgcodecs.imwrite(
            basePath + "target/test-classes/perspectiveshift/debug2.jpg",
            shifted);
      }
    }
  //}

}