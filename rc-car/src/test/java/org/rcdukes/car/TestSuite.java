package org.rcdukes.car;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestHandlers.class })
/**
 * TestSuite for car aka Bo
 * 
 * @author wf
 *
 *         no content necessary - annotation has info
 */
public class TestSuite {
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
