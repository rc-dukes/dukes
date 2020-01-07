package nl.vaneijndhoven.dukes.imageview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import static org.asynchttpclient.Dsl.*;
import org.junit.Test;

import javafx.scene.image.Image;

import java.util.concurrent.Future;
import nl.vaneijndhoven.dukes.common.ClusterStarter;
import nl.vaneijndhoven.dukes.common.DukesVerticle.Status;
import nl.vaneijndhoven.dukes.common.Environment;
import nl.vaneijndhoven.opencv.video.ImageUtils;

/**
 * test the imageView verticle
 * @author wf
 *
 */
public class TestDebugImageServer {
  private ClusterStarter clusterStarter;
  private DebugImageServer imageServer;

  public void startImageServer() throws Exception {
    Environment.mock();
    clusterStarter=new ClusterStarter();
    imageServer = new DebugImageServer();
    clusterStarter.deployVerticles(imageServer);
    imageServer.waitStatus(Status.started,20000,10);
    assertNotNull(imageServer.deploymentID());
  }
  @Test
  public void testDebugImageServerStart() throws Exception {
    startImageServer();
    clusterStarter.undeployVerticle(imageServer);
    imageServer.waitStatus(Status.stopped,20000,10);
  }
  
  @Test
  public void testTestImage() throws Exception {
    startImageServer();
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
