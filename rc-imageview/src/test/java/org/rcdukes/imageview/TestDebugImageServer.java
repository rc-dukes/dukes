package org.rcdukes.imageview;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.Future;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.junit.Test;
import org.rcdukes.common.ClusterStarter;
import org.rcdukes.common.DukesVerticle.Status;
import org.rcdukes.common.Environment;
import org.rcdukes.detect.ImageFetcher;
import org.rcdukes.video.ImageUtils;

import javafx.scene.image.Image;

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
    DebugImageServer.SERVE_TEST_IMAGES=true;
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
  
  @Test
  public void testMJpegStream() throws Exception {
    // Dorf Appenzell
    String url="http://213.193.89.202/axis-cgi/mjpg/video.cgi";
    //url="http://localhost:8081?type=simulator&mode=stream";
    //url="http://picarford:8080/?action=stream";
    MJpegHandler mjpegHandler=new MJpegHandler(url);
    MJpegDecoder.debug=true;
    int bufferSize = 1024 * 64; // 64 KByte Buffer
    MJpegDecoder mjpegDecoder = mjpegHandler.open(bufferSize);
    Thread.sleep(1000);
    mjpegDecoder.close();
  }
  
  @Test
  public void testOpenCV() {
    String url="http://213.193.89.202/axis-cgi/mjpg/video.cgi";
    ImageFetcher imageFetcher=new ImageFetcher(url);
    for (int frame=0;frame<=50;frame++) {
      imageFetcher.fetch();
    }
    imageFetcher.close();
  }
  
}
