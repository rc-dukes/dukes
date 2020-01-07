package nl.vaneijndhoven.dukes.imageview;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import nl.vaneijndhoven.dukes.common.ClusterStarter;
import nl.vaneijndhoven.dukes.common.Environment;
import nl.vaneijndhoven.dukes.common.DukesVerticle.Status;

/**
 * test the imageView verticle
 * @author wf
 *
 */
public class TestDebugImageServer {
  @Test
  public void testDebugImageServerStart() throws Exception {
    Environment.mock();
    ClusterStarter clusterStarter=new ClusterStarter();
    DebugImageServer imageServer=new DebugImageServer();
    clusterStarter.deployVerticles(imageServer);
    imageServer.waitStatus(Status.started,20000,10);
    assertNotNull(imageServer.deploymentID());
    clusterStarter.undeployVerticle(imageServer);
    imageServer.waitStatus(Status.stopped,20000,10);
  }
  
}
