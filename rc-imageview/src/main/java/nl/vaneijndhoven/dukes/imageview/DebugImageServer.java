package nl.vaneijndhoven.dukes.imageview;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.opencv.core.Mat;
import org.rcdukes.detect.Detector;

import io.vertx.core.Future;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;
import nl.vaneijndhoven.dukes.common.Characters;
import nl.vaneijndhoven.dukes.common.Config;
import nl.vaneijndhoven.dukes.common.DukesVerticle;
import nl.vaneijndhoven.dukes.common.Events;
import nl.vaneijndhoven.opencv.video.ImageUtils;

/**
 * Imageview verticle (Rosco)
 *
 */
public class DebugImageServer extends DukesVerticle {

  /**
   * construct me with my character information
   */
  public DebugImageServer() {
    super(Characters.ROSCO);
  }

  protected HttpServer server;
  // @TODO Make configurable
  // the format to be used for image encoding
  // needs to be jpg to be able to record videos
  public static enum ImageFormat {png,jpg}
  public static ImageFormat imageFormat=ImageFormat.jpg;
  public static String exts[] = {".png",".jpg"};
  public static String contentTypes[] = {"image/png","image/jpeg"};
  // @TODO Make configurable and adapt
  public static double fps=10;
  public static String defaultImage = "images/640px-4_lane_highway_roads_in_India_NH_48_Karnataka_3";
  private static byte[] testImageBytes;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    super.preStart();
    // @TODO - get config information from config Verticle (or shared data ...)
    int port = Config.getEnvironment().getInteger(Config.IMAGEVIEW_PORT);
    server = vertx.createHttpServer().requestHandler(this::sendImage);
    // Now bind the server:
    server.listen(port, res -> {
      if (res.succeeded()) {
        startFuture.complete();
        consumer(Events.START_RECORDING, x -> startRecording());
        consumer(Events.STOP_RECORDING, x -> stopRecording());
        super.postStart();
        String msg = String.format(
            "web server on port %d serving debug images in %s format", port,
            exts[imageFormat.ordinal()]);
        LOG.info(msg);
      } else {
        startFuture.fail(res.cause());
      }
    });

  }

  Map<String, VideoRecorder> recorders = new HashMap<String, VideoRecorder>();

  enum ImageType {
    camera, edges, birdseye, lines
  }

  /**
   * start Recording
   */
  protected void startRecording() {
    for (ImageType imageType : ImageType.values()) {
      VideoRecorder recorder = new VideoRecorder(imageType.name(),fps);
      recorders.put(recorder.name, recorder);
    }
  }

  /**
   * stop Recording
   */
  protected void stopRecording() {
    for (VideoRecorder recorder : recorders.values()) {
      recorder.stop();
      recorders.remove(recorder.name);
    }
  }

  /**
   * send an image for the given request
   * 
   * @param request
   */
  public void sendImage(HttpServerRequest request) {
    String ext=exts[imageFormat.ordinal()];
    String type = request.getParam("type");
    // System.out.println(type);
    if (type == null)
      type = "camera";
    byte[] bytes = null;
    Mat mat=null;
    switch (type) {
    case "edges":
      bytes = Detector.CANNY_IMG;
      mat=ImageUtils.imageBytes2Mat(bytes);
      break;

    case "birdseye":
      mat=Detector.BIRDS_EYE;
      bytes = ImageUtils.mat2ImageBytes(mat, ext);
      break;
      
    case "lines":
      mat=Detector.MAT;
      bytes = ImageUtils.mat2ImageBytes(mat, ext);
      break;
      
    case "camera":
      mat=Detector.camera;
      bytes = ImageUtils.mat2ImageBytes(mat, ext);
      break;
    }
    sendImageBytesOrDefault(request, bytes);
    if (recorders.containsKey(type) && mat!=null) {
      VideoRecorder recorder = recorders.get(type);
      recorder.recordMat(mat);
    }
  }

  /**
   * supply a default testImage
   * @return - the bytes of the test Image
   */
  public static byte[] testImage() {
    if (testImageBytes == null) {
      try {
        String ext=exts[imageFormat.ordinal()];
        String testImagePath = defaultImage + ext;
        testImageBytes = IOUtils.toByteArray(DebugImageServer.class
            .getClassLoader().getResourceAsStream(testImagePath));
      } catch (IOException e) {
        LOG.trace(e.getMessage());
      }
    }
    return testImageBytes;
  }

  /**
   * send the given bytes if they are not null or replace with a default image
   * 
   * @param request
   *          - the request to respond to
   * @param bytes
   *          - the bytes to be send
   */
  private void sendImageBytesOrDefault(HttpServerRequest request,
      byte[] bytes) {
    if (bytes == null) {
      bytes=testImage();
    }
    if (bytes != null)
      this.sendImageBytes(request, bytes);
  }

  /**
   * send the given bytes as an image
   * 
   * @param bytes
   */
  public void sendImageBytes(HttpServerRequest request, byte[] bytes) {
    HttpServerResponse response = request.response();
    String contentType=contentTypes[imageFormat.ordinal()];
    response.putHeader("content-type", contentType);
    response.putHeader("content-length", "" + bytes.length);
    Buffer data = Buffer.buffer().appendBytes(bytes);
    response.write(data);
    response.close();
  }
}