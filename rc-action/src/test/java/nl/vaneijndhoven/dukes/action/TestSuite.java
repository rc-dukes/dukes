package nl.vaneijndhoven.dukes.action;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestAction.class })
/**
 * TestSuite for action aka luke
 * 
 * @author wf
 *
 *         no content necessary - annotation has info
 */
public class TestSuite {
  public static boolean debug=false;
  /**
   * check if we are in the Travis-CI environment
   * 
   * @return true if Travis user was detected
   */
  public static boolean isTravis() {
    String user = System.getProperty("user.name");
    return user.equals("travis");
  }
}
