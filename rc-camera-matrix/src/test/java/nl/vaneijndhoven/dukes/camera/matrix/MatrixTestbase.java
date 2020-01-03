package nl.vaneijndhoven.dukes.camera.matrix;

/**
 * common parts of opencv Matrix based tests
 * @author wf
 *
 */
public class MatrixTestbase {
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
