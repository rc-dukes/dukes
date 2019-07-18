package nl.vaneijndhoven.dukes.action;

import static org.junit.Assert.*;

import org.junit.Test;

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
	}

}
