package org.rcdukes.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;

import stormbots.Main;
import stormbots.MiniPID;

/**
 * test MiniPID see <a href=
 * 'http://brettbeauregard.com/blog/2011/04/improving-the-beginners-pid-direction/improving-the-beginners-pid-introduction'>Improving
 * the Beginners PID introduction</a> see
 * <a href='https://en.wikipedia.org/wiki/PID_controller'>Proportional Integral
 * Derivative Controller</a>
 * 
 * @author wf
 *
 */
public class TestMiniPID {

	@Test
	public void testMiniPID() {
		double sensorValues[] = { 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 };
		MiniPID pid = new MiniPID(1, 0, 0);
		pid.reset();
		double target=1.0;
		double epsilon=0.000001;
		pid.setOutputLimits(0,target);
		assertEquals(0.0, pid.getOutput(), epsilon);
		for (double actual : sensorValues) {
			assertEquals(target-actual,pid.getOutput(actual, target),epsilon);
		}
		pid.setI(0);
		pid.setD(0.5);
		for (double actual : sensorValues) {
			System.out.println(pid.getOutput(actual*actual, target));
		}
	}
	
	@Test
	public void testMain() throws IOException {
	  redirectStdErr();
		String args[]= {};
		Main.main(args);
	  String stderr = restoreStdErr();
    // System.err.println(stderr);
    assertTrue(stderr.startsWith("Target | Actual | Output | Error\n" + 
        "=======+========+========+========\n" + 
        "100.00 |  10.00 |  10.00 |  90.00\n" + 
        "100.00 |  16.40 |   6.40 |  83.60\n" + 
        "100.00 |  24.64 |   8.24 |  75.36\n" + 
        "100.00 |  32.54 |   7.90 |  67.46\n" + 
        "100.00 |  40.98 |   8.44 |  59.02\n" + 
        "100.00 |  49.61 |   8.62 |  50.39\n" + 
        "100.00 |  58.56 |   8.95 |  41.44"));
	}

  static PrintStream err;
  static ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  // static PrintStream out;

  public static void redirectStdErr() {
    err = System.err;
    System.setErr(new PrintStream(errContent));
    // System.err.println("Catching messages on System.err ...");
  }

  /**
   * restore std err and return the catched result
   * 
   * @return the string content of the grabbed stderr
   * @throws IOException
   */
  public static String restoreStdErr() throws IOException {
    errContent.flush();
    String stderr = new String(errContent.toByteArray(), "utf-8");
    // restore original error handling
    System.setErr(err);
    return stderr;
  }

}
