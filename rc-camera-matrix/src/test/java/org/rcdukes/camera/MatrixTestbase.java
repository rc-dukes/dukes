package org.rcdukes.camera;

import org.junit.BeforeClass;

import com.bitplan.opencv.NativeLibrary;

/**
 * common parts of opencv Matrix based tests
 * @author wf
 *
 */
public class MatrixTestbase {
  @BeforeClass
  public static void setup() throws Exception {
    NativeLibrary.load();
  }
  
  String basePath = "./";
  public static boolean debug=true;
  /**
   * check if we are in the Travis-CI environment
   * 
   * @return true if Travis user was detected
   */
  public boolean isTravis() {
    String user = System.getProperty("user.name");
    return user.equals("travis");
  }
}
