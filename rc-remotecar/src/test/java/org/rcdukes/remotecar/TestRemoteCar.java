package org.rcdukes.remotecar;

import org.junit.Test;
import org.rcdukes.common.Characters;
import org.rcdukes.common.ClusterStarter;
import org.rcdukes.common.Config;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.DukesVerticle.Status;
import org.rcdukes.common.Environment;

import io.vertx.core.json.JsonObject;

/**
 * test the remote car handling
 * 
 * @author wf
 *
 */
public class TestRemoteCar {

  @Test
  public void testRemoteCar() throws Exception {
    Environment.mock();
    ClusterStarter clusterStarter = new ClusterStarter();
    clusterStarter.prepare();
    RemoteCar remoteCar = new RemoteCar();
    clusterStarter.deployVerticles(remoteCar);
    DukesVerticle.debug=true;
    remoteCar.waitStatus(Status.started, ClusterStarter.MAX_START_TIME, 10);
  
    JsonObject configJo=Config.getEnvironment().asJsonObject();
    remoteCar.send(Characters.GENERAL_LEE,configJo);
    Thread.sleep(ClusterStarter.MAX_START_TIME);
  }

}
