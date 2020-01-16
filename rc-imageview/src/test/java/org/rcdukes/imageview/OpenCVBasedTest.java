package org.rcdukes.imageview;

import org.junit.BeforeClass;
import org.rcdukes.opencv.NativeLibrary;

public class OpenCVBasedTest {
  @BeforeClass
  public static void setup() throws Exception {
    NativeLibrary.load();
  }
}
