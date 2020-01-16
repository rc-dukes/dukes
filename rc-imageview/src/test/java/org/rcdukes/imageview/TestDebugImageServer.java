package org.rcdukes.imageview;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.Future;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.junit.Test;
import org.rcdukes.video.ImageUtils;

import javafx.scene.image.Image;
import org.rcdukes.common.ClusterStarter;
import org.rcdukes.common.DukesVerticle.Status;
import org.rcdukes.imageview.DebugImageServer;
import org.rcdukes.common.Environment;

/**
 * test the imageView verticle
 * @author wf
 *
 */
public class TestDebugImageServer extends OpenCVBasedTest {
  private ClusterStarter clusterStarter;
  private DebugImageServer imageServer;
  int TIME_OUT=20000;

  public void startImageServer() throws Exception {
    Environment.mock();
    clusterStarter=new ClusterStarter();
    imageServer = new DebugImageServer();
    clusterStarter.deployVerticles(imageServer);
    imageServer.waitStatus(Status.started,TIME_OUT,10);
    assertNotNull(imageServer.deploymentID());
  }
  @Test
  public void testDebugImageServerStart() throws Exception {
    startImageServer();
    clusterStarter.undeployVerticle(imageServer);
    imageServer.waitStatus(Status.stopped,TIME_OUT,10);
  }
  
  @Test
  public void testTestImage() throws Exception {
    startImageServer();
    // @TODO Make configurable
    String url="http://localhost:8081";
    AsyncHttpClient asyncHttpClient = asyncHttpClient();
    Future<Response> whenResponse = asyncHttpClient.prepareGet(url).execute();
    Response response = whenResponse.get();    
    assertEquals(200,response.getStatusCode());
    byte[] imageBytes = response.getResponseBodyAsBytes();
    Image image = ImageUtils.imageBytes2Image(imageBytes);
    assertNotNull(image);
    assertEquals(640,Math.round(image.getWidth()));
    assertEquals(480,Math.round(image.getHeight()));
  }
  
}
