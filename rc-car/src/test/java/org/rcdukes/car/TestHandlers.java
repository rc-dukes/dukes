package org.rcdukes.car;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.rcdukes.car.SteeringHandler;
import org.rcdukes.common.Config;
import org.rcdukes.common.ServoPosition;

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
  

  @Test
  public void testSteeringAngles() throws Exception {
    Car car=getCar();
    // make sure commands are accepted
    car.setPowerOn();
    SteeringHandler sh=new SteeringHandler(car);
    double angles[]= {-45,-30,-20,-10,-5,0,5,10,20,30,45};
    double expectedValues[] = {-20,-20,-20,-10,-5,0,5,10,20,25,25};
    int index=0;
    for (double angle:angles) {
      JsonObject angleJo=new JsonObject();
      angleJo.put("angle", new Double(angle));
      ServoPosition anglePos = sh.handleServoAngle(angleJo);
      String msg=String.format("steering %5.1f° (%3d) for wanted angle %5.1f°", anglePos.getValue(),anglePos.getServoPos(),angle);
      if (debug)
        System.out.println(msg);
      assertEquals(expectedValues[index],anglePos.getValue(),0.001);
      index++;
    }
  }

}
