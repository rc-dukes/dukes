package nl.vaneijndhoven.dukes.imageview;

import org.junit.Test;

import nl.vaneijndhoven.dukes.common.ClusterStarter;
import nl.vaneijndhoven.dukes.common.Environment;

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
    imageServer.waitStarted(20000,10);
  }
  
}
