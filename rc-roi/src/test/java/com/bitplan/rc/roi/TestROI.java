package com.bitplan.rc.roi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.bitplan.opencv.NativeLibrary;

import nl.vaneijndhoven.dukes.roi.FarField;
import nl.vaneijndhoven.dukes.roi.LeftField;
import nl.vaneijndhoven.dukes.roi.NearField;
import nl.vaneijndhoven.dukes.roi.ROI;
import nl.vaneijndhoven.dukes.roi.RightField;

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
    //if (!isTravis()) {
      if (debug)
        System.out
            .println(String.format("trying to load native library %s from path",
                Core.NATIVE_LIBRARY_NAME));
      assertTrue(NativeLibrary.getNativeLibPath().isDirectory());
      assertTrue(NativeLibrary.getNativeLib().getCanonicalPath(),NativeLibrary.getNativeLib().isFile());
      Exception issue=null;
      try {
        NativeLibrary.load();
      } catch (Exception le) {
    	  issue=le;
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
      if (issue!=null)
    	  throw issue;
    //} isTravis
  }

  @Test
  public void testROI() throws Exception {
    if (!isTravis()) {
      NativeLibrary.load();
      File imgRoot = new File(testPath);
      assertTrue(imgRoot.isDirectory());
      Mat image = Imgcodecs.imread(testPath + "/test_image.jpg");
      assertNotNull(image);
      assertEquals(960, image.height());
      assertEquals(1280, image.width());
      ROI rois[] = { new FarField(0.5), new NearField(0.5),
          new LeftField(0.5), new RightField(0.5) };
      for (ROI roi : rois) {
        Mat roiImage = roi.region(image);
        if (debug)
          System.out.println(
              String.format("%10s: %4d x %4d", roi.getClass().getSimpleName(),
                  roiImage.width(), roiImage.height()));
        assertEquals(roiImage.width(), image.width() * roi.getWidthFraction(),
            0.1);
        assertEquals(roiImage.height(),
            image.height() * roi.getHeightFraction(), 0.1);
      }
    }
  }

}
