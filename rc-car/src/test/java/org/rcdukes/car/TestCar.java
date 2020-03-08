package org.rcdukes.car;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.rcdukes.car.CarVerticle;

import org.rcdukes.common.ClusterStarter;
import org.rcdukes.common.Environment;
import org.rcdukes.common.ServoPosition;
import org.rcdukes.common.DukesVerticle.Status;

import io.vertx.core.json.JsonObject;

/**
 * test the car verticle
 * @author wf
 *
 */
public class TestCar {
  public static boolean debug=true;
  @Test
  public void testCar() throws Exception {
    int TIME_OUT=40000;
    Environment.mock();
    ClusterStarter clusterStarter=new ClusterStarter();
    CarVerticle carVerticle = new CarVerticle();
    clusterStarter.deployVerticles(carVerticle);
    carVerticle.waitStatus(Status.started,TIME_OUT,10);
    if (!TestSuite.isTravis()) {
      clusterStarter.undeployVerticle(carVerticle);
      carVerticle.waitStatus(Status.stopped,TIME_OUT,10);
    }
  }
  
  @Test
  public void testServoPositionMapping() {
    ServoPosition[] poss= {new ServoPosition(154,10,"°","steering"),new ServoPosition(130,0,"m/s","motor")};
    for (ServoPosition pos:poss) {
      JsonObject jo=JsonObject.mapFrom(pos);
      String json=jo.encodePrettily();
      assertNotNull(json);
      if (debug)
        System.out.println(json);
      ServoPosition rpos=jo.mapTo(ServoPosition.class);
      assertEquals(pos.kind,rpos.kind);
      assertEquals(pos.getServoPos(),rpos.getServoPos());
      assertEquals(pos.getValue(),rpos.getValue(),0.001);
      assertEquals(pos.unit,rpos.unit);
    }
  }
}
