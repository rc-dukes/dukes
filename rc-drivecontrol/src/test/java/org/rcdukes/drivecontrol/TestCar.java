package org.rcdukes.drivecontrol;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.rcdukes.car.Engine;
import org.rcdukes.car.Led;
import org.rcdukes.car.Steering;
import org.rcdukes.drivecontrol.Car;

import org.rcdukes.common.Config;
import org.rcdukes.common.Environment;

/**
 * test the handling of the car
 * 
 * @author wf
 *
 */
public class TestCar {
	public static boolean debug = true;

	protected static ServoCommandDummy servoCommand;

	public static Car getCar() throws Exception {
		// Let's fake a car instance with some properties
		Environment.mock();
		Environment env = Config.getEnvironment();
		assertEquals("pi.doe.com", env.getString(Config.REMOTECAR_HOST));
		// Let's fake a ServoCommand
		servoCommand = new ServoCommandDummy();
		servoCommand.resetLog();
		Thread.sleep(1);
		Car.servoCommand = servoCommand;
		// get a car with the above settings
		Car.resetInstance();
		Car car = Car.getInstance();
		return car;
	}

	@Test
	public void testCar() throws Exception {
		Car car = getCar();
		// let's play ...
		car.setPowerOn();
		car.turn(-1);
		car.turn(120);
		car.turn(1000);
		car.drive(-1);
		car.drive(150);
		car.drive(1000);
		car.setPowerOff();
		// Let's see what we got up to here ...
		if (debug)
			servoCommand.showLog();
	}

	@Test
	public void testEngine() throws Exception {
		Car car = getCar();
		Engine engine = car.getEngine();
		engine.forceInNeutral();
		engine.setSpeed(14); // ignored - no power
		car.setPowerOn();
		engine.setSpeed(140);
		if (debug)
			servoCommand.showLog();
	}

	@Test
	public void testSteering() throws Exception {
		Car car = getCar();
		Steering steering = car.getSteering();
		steering.center();
		steering.setWheelPosition(145);
	}

	@Test
	public void testLed() throws Exception {
		Car car = getCar();

		Led led = car.getLed();
		led.statusLedOn();
		led.statusLedOff();
		// Let's see what we got up to here ...
		if (debug)
			servoCommand.showLog();
	}
}
