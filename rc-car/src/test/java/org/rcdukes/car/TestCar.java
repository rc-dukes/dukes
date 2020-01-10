package org.rcdukes.car;

import org.junit.Test;
import org.rcdukes.car.CarVerticle;

import nl.vaneijndhoven.dukes.common.ClusterStarter;
import nl.vaneijndhoven.dukes.common.Environment;
import nl.vaneijndhoven.dukes.common.DukesVerticle.Status;

public class TestCar {
  @Test
  public void testCar() throws Exception {
    int TIME_OUT=20000;
    Environment.mock();
    ClusterStarter clusterStarter=new ClusterStarter();
    CarVerticle carVerticle = new CarVerticle();
    clusterStarter.deployVerticles(carVerticle);
    carVerticle.waitStatus(Status.started,TIME_OUT,10);
    //if (!TestSuite.isTravis()) {
      clusterStarter.undeployVerticle(carVerticle);
      carVerticle.waitStatus(Status.stopped,TIME_OUT,10);
    //}
  }
}