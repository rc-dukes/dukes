package nl.vaneijndhoven.dukes.imageview;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.opencv.core.Mat;

import io.vertx.core.Future;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;
import nl.vaneijndhoven.detect.Detector;
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
  public static String ext = ".png";
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
        super.postStart();
        String msg = String.format(
            "web server on port %d serving debug images in %s format", port,
            ext);
        LOG.info(msg);
      } else {
        startFuture.fail(res.cause());
      }
    });

  }

  Map<String, VideoRecorder> recorders = new HashMap<String, VideoRecorder>();

  enum ImageType {
    camera, debug, edges, birdseye
  }

  /**
   * start Recording
   */
  protected void startRecording() {
    for (ImageType imageType : ImageType.values()) {
      boolean isColor=!imageType.equals(ImageType.edges);
      VideoRecorder recorder = new VideoRecorder(imageType.name(),isColor);
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
    String type = request.getParam("type");
    if (type == null)
      type = "debug";
    byte[] bytes = null;
    Mat mat;
    switch (type) {
    case "edges":
      bytes = Detector.CANNY_IMG;
      mat=ImageUtils.imageBytes2Mat(bytes);
      break;

    case "birdseye":
      mat=Detector.BIRDS_EYE;
      bytes = ImageUtils.mat2ImageBytes(mat, ext);
      break;
      
    default: // debug
      mat=Detector.MAT;
      bytes = ImageUtils.mat2ImageBytes(mat, ext);
    }
    sendImageBytesOrDefault(request, bytes);
    if (recorders.containsKey(type) && mat!=null) {
      VideoRecorder recorder = recorders.get(type);
      if (!recorder.started) {
        // @TODO make configurable
        double fps=25.0;
        recorder.start(fps,mat.size());
      }
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
    String contentType=String.format("image/%s",ext.replace(".", ""));
    response.putHeader("content-type", contentType);
    response.putHeader("content-length", "" + bytes.length);
    Buffer data = Buffer.buffer().appendBytes(bytes);
    response.write(data);
  }
}