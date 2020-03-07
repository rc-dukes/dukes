package org.rcdukes.car;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.rcdukes.car.SteeringHandler;
import org.rcdukes.common.Config;

import io.vertx.core.json.JsonObject;
import org.rcdukes.car.Steering;
import org.rcdukes.drivecontrol.Car;
import org.rcdukes.drivecontrol.TestCar;

/**
 * test the speed and engine handlers
 * 
 * @author wf
 *
 */
public class TestHandlers extends TestCar {

  public static boolean debug=true;
  
 
  @Test
  public void testSteeringHandler() throws Exception {
    Car car=getCar();
    // make sure commands are accepted
    car.setPowerOn();
    SteeringHandler sh=new SteeringHandler(car);
    Steering steering = car.getSteering();
    JsonObject msg=new JsonObject();
    msg.put("position",Config.POSITION_CENTER);
    sh.handleServo(msg);
    assertEquals(130,steering.getServo());
    msg.put("position",Config.POSITION_LEFT);
    sh.handleServo(msg);
    assertEquals(125,steering.getServo());
    msg.put("position",Config.POSITION_LEFT);
    sh.handleServo(msg);
    assertEquals(120,steering.getServo());
    if (debug)
      servoCommand.showLog();
  }

}
