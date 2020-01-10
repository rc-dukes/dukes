package org.rcdukes.car;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.rcdukes.car.SteeringHandler;

import io.vertx.core.json.JsonObject;
import nl.vaneijndhoven.dukes.car.Steering;
import nl.vaneijndhoven.dukes.common.Config;
import nl.vaneijndhoven.dukes.drivecontrol.Car;
import nl.vaneijndhoven.dukes.drivecontrol.TestCar;

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
    assertEquals(135,steering.getServo());
    msg.put("position",Config.POSITION_LEFT);
    sh.handleServo(msg);
    assertEquals(140,steering.getServo());
    if (debug)
      servoCommand.showLog();
  }

}
