package nl.vaneijndhoven.dukes.cooter;

import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.bitplan.opencv.NativeLibrary;

import nl.vaneijndhoven.dukes.camera.matrix.CameraMatrix;

/**
 * test for camera matrix
 *
 */
public class TestCameraMatrix extends MatrixTestbase {
  
  String testPath=basePath+ "target/test-classes/cameramatrix/";
  
  @Test
  public void testCalculation() throws Exception {
    NativeLibrary.load();
    
    CameraMatrix matrix = new CameraMatrix(8, 6);

    ArrayList<Mat> images = new ArrayList<>();
    Files
        .newDirectoryStream(
            Paths.get(testPath),
            path -> path.getFileName().toString().startsWith("GOPR"))
        .forEach(path -> {
          System.out.println("reading: " + path);
          String substring = path.toString();
          Mat image = Imgcodecs.imread(substring);
          matrix.calibrate(image);
        });

    images.forEach(Mat::release);

    System.out.println(matrix.serialize());

    CameraMatrix deserializedMatrix = CameraMatrix
        .deserizalize(matrix.serialize());

    Mat image = Imgcodecs
        .imread(testPath+"/test_image.jpg");

    assertEquals(matrix.serialize(), deserializedMatrix.serialize());

    Mat undistorted = deserializedMatrix.apply(image);

    System.out.println(deserializedMatrix.serialize());

    if (debug)
      Imgcodecs.imwrite(testPath + "debug.jpg",
          undistorted);

  }

}