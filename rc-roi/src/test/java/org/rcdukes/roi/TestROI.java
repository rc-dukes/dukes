package org.rcdukes.roi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.rcdukes.opencv.NativeLibrary;

/**
 * Test the Region of interest handling
 * 
 * @author wf
 */
public class TestROI {
  public static boolean debug = true;

  /**
   * check if we are in the Travis-CI environment
   * 
   * @return true if Travis user was detected
   */
  public boolean isTravis() {
    String user = System.getProperty("user.name");
    return user.equals("travis");
  }

  String basePath = "./";
  String testPath = basePath + "target/test-classes/roi/";

  /**
   * @see <a href=
   *      'https://stackoverflow.com/questions/27088934/unsatisfiedlinkerror-no-opencv-java249-in-java-library-path/35112123#35112123'>OpenCV
   *      native libraries</a>
   * @throws Exception
   */
  @Test
  public void testNativeLibrary() throws Exception {

    if (debug)
      System.out
          .println(String.format("trying to load native library %s from path",
              Core.NATIVE_LIBRARY_NAME));
    assertTrue(NativeLibrary.getNativeLibPath().isDirectory());
    assertTrue(NativeLibrary.getNativeLib().getCanonicalPath(),
        NativeLibrary.getNativeLib().isFile());
    Exception issue = null;
    try {
      NativeLibrary.load();
    } catch (Exception le) {
      issue = le;
    }
    for (String pathelement : NativeLibrary.getCurrentLibraryPath()) {
      File path = new File(pathelement);
      if (debug)
        System.out.print("\t" + pathelement);
      if (path.isDirectory()) {
        if (debug)
          System.out.println("✓");
        File libFile = new File(path, NativeLibrary.getLibraryFileName());
        if (libFile.isFile() && debug) {
          System.out.println(
              String.format("found at %s ", libFile.getCanonicalPath()));
        }
      } else {
        if (debug)
          System.out.println();
      }
    }
    if (issue != null)
      throw issue;
  }

  @Test
  public void testLogStderr() throws Exception {
    NativeLibrary.logStdErr();
    /* even the nullStream approach does not work
    PrintStream nullStream = new PrintStream(new OutputStream() {
      public void write(int b) {
      }
    });
    System.setErr(nullStream);
    System.setOut(nullStream);*
    */
    System.err.println("testing stderr via slf4j");

    NativeLibrary.load();
    VideoCapture capture = new VideoCapture();
    // Dorf Appenzell
    // String url="http://213.193.89.202/axis-cgi/mjpg/video.cgi";
    // Logitech Cam on test car
    // url="http://picarford:8080/?action=stream";
    File imgRoot = new File(testPath);
    File testStream = new File(imgRoot, "logitech_test_stream.mjpg");
    assertTrue(testStream.canRead());
    capture.open(testStream.getPath());
    Mat image = new Mat();
    capture.read(image);
    assertEquals(640, image.width());
    assertEquals(480, image.height());
    capture.release();
  }

  @Test
  public void testROI() throws Exception {
    if (!isTravis()) {
      NativeLibrary.load();
      File imgRoot = new File(testPath);
      assertTrue(imgRoot.isDirectory());
      Mat image = Imgcodecs.imread(testPath + "dukes_roi_test_image.jpg");
      assertNotNull(image);
      assertEquals(960, image.height());
      assertEquals(1280, image.width());
      ROI rois[] = { new ROI("all", 0, 0, 1, 1), new ROI("far", 0, 0, 1, 0.5),
          new ROI("near", 0, 0.5, 1, 0.5), new ROI("left", 0, 0, 0.5, 1),
          new ROI("right", 0.5, 0, 0.5, 1) };
      for (ROI roi : rois) {
        System.out.println(roi);
        Mat roiImage = roi.region(image);
        if (debug)
          System.out.println(String.format("%10s: %4d x %4d", roi.name,
              roiImage.width(), roiImage.height()));
        assertEquals(roiImage.width(), image.width() * roi.rw, 0.1);
        assertEquals(roiImage.height(), image.height() * roi.rh, 0.1);
        this.writeImage("dukes_roi_" + roi.name, roiImage);
      }
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
