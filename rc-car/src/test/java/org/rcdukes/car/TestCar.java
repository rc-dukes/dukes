package org.rcdukes.car;

import org.junit.Test;
import org.rcdukes.car.CarVerticle;

import org.rcdukes.common.ClusterStarter;
import org.rcdukes.common.Environment;
import org.rcdukes.common.DukesVerticle.Status;

public class TestCar {
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
}
