package com.bitplan.rc.roi;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opencv.core.Core;

import com.bitplan.opencv.NativeLibrary;

/**
 * Test the Region of interest handling
 * 
 * @author wf
 */
public class TestROI {
  public static boolean debug = true;

  /**
   * @see <a href=
   *      'https://stackoverflow.com/questions/27088934/unsatisfiedlinkerror-no-opencv-java249-in-java-library-path/35112123#35112123'>OpenCV
   *      native libraries</a>
   * @throws Exception
   */
  @Test
  public void testNativeLibrary() throws Exception {
    if (debug)
      System.out.println(String.format("trying to load native library %s",
          Core.NATIVE_LIBRARY_NAME));
    assertTrue(NativeLibrary.getNativeLibPath().isDirectory());
    assertTrue(NativeLibrary.getNativeLib().isFile());
    NativeLibrary.load();
  }

  @Test
  public void testROI() throws Exception {
    NativeLibrary.load();
  }

}
