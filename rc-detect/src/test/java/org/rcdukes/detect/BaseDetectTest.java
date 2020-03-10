package org.rcdukes.detect;

import org.junit.BeforeClass;
import org.opencv.core.Mat;
import org.rcdukes.opencv.NativeLibrary;
import org.rcdukes.video.ImageUtils;

/**
 * base class for detection tests
 * 
 * @author wf
 *
 */
public class BaseDetectTest {
  boolean debug = true;

  @BeforeClass
  public static void setup() throws Exception {
    NativeLibrary.load();
  }

  public Mat getTestImage() throws Exception {
    String imagePath="images/road.jpg";
    Mat frame = ImageUtils.fromResource(this.getClass(), imagePath);
    return frame;
  }

}
