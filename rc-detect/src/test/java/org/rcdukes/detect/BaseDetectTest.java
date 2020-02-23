package org.rcdukes.detect;

import org.junit.BeforeClass;
import org.opencv.core.Mat;
import org.rcdukes.opencv.NativeLibrary;
import org.rcdukes.video.ImageUtils;

/**
 * base class for detection tests
 * @author wf
 *
 */
public class BaseDetectTest {
  boolean debug = true;

  @BeforeClass
  public static void setup() throws Exception {
    NativeLibrary.load();
  }
  String testImageUrl="https://upload.wikimedia.org/wikipedia/commons/thumb/f/f2/4_lane_highway_roads_in_India_NH_48_Karnataka_3.jpg/1280px-4_lane_highway_roads_in_India_NH_48_Karnataka_3.jpg";

  public Mat getTestImage() throws Exception {
    Mat frame=ImageUtils.read(testImageUrl);
    return frame;
  }
  
}
