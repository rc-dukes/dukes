package org.rcdukes.imageview;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.rcdukes.common.Characters;
import org.rcdukes.common.Config;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.Events;
import org.rcdukes.detect.Detector;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageCollector;
import org.rcdukes.video.ImageCollector.ImageType;
import org.rcdukes.video.ImageUtils;

import io.vertx.core.Future;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;

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
  public static enum ImageFormat {
    png, jpg
  }

  public static ImageFormat imageFormat = ImageFormat.jpg;
  public static String exts[] = { ".png", ".jpg" };
  public static String contentTypes[] = { "image/png", "image/jpeg" };
  // @TODO Make configurable and adapt
  public static double fps = 10;
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

  Map<ImageType, VideoRecorder> recorders = new HashMap<ImageType, VideoRecorder>();

  /**
   * start Recording
   */
  protected void startRecording() {
    for (ImageType imageType : ImageType.values()) {
      VideoRecorder recorder = new VideoRecorder(imageType.name(), fps);
      recorders.put(imageType, recorder);
    }
  }

  /**
   * stop Recording
   */
  protected void stopRecording() {
    for (Entry<ImageType, VideoRecorder> entry : recorders.entrySet()) {
      VideoRecorder recorder = entry.getValue();
      ImageType imageType = entry.getKey();
      recorder.stop();
      recorders.remove(imageType);
    }
  }

  /**
   * send an image for the given request
   * 
   * @param request
   */
  public void sendImage(HttpServerRequest request) {
    String type = request.getParam("type");
    String mode = request.getParam("mode");
    if (type == null)
      type = "camera";
    ImageType imageType = ImageType.valueOf(type);
    if (mode != null && mode.equals("stream")) {
      sendStream(request, imageType);
    } else {
      sendSingleImage(request, imageType);
    }
  }

  /**
   * send a single Image
   * 
   * @param request
   * @param imageType
   */
  public void sendSingleImage(HttpServerRequest request, ImageType imageType) {
    ImageCollector collector = Detector.getImageCollector();
    Image image = null;
    Mat mat = null;
    image = collector.getImage(imageType, true);
    if (image != null) {
      mat = image.getFrame();
      this.sendImageBytes(request, image.getImageBytes());
    }
    if (recorders.containsKey(imageType) && mat != null) {
      VideoRecorder recorder = recorders.get(imageType);
      recorder.recordMat(mat);
    }
  }

  /**
   * send the given bytes as an image
   * 
   * @param bytes
   */
  public void sendImageBytes(HttpServerRequest request, byte[] bytes) {
    HttpServerResponse response = request.response();
    String contentType = contentTypes[imageFormat.ordinal()];
    response.putHeader("content-type", contentType);
    response.putHeader("content-length", "" + bytes.length);
    Buffer data = Buffer.buffer().appendBytes(bytes);
    response.write(data);
    response.close();
  }

  /**
   * stream images
   * 
   * @param request
   * @param imageType
   */
  private void sendStream(HttpServerRequest request, ImageType imageType) {
    HttpServerResponse response = request.response();
    Runnable multipartStreamer = new MultipartStreamer(response, imageType);
    Thread streamThread = new Thread(multipartStreamer);
    streamThread.start();
  }

  /**
   * Streamer for multipart images
   * 
   * @author wf
   *
   */
  static class MultipartStreamer implements Runnable {
    String crlf = "\r\n";
    boolean running = false;
    private HttpServerResponse response;
    private ImageType imageType;
    String boundary = "frame";

    /**
     * create a MultipartStream for the given imageType
     * 
     * @param response
     * @param imageType
     */
    public MultipartStreamer(HttpServerResponse response, ImageType imageType) {
      this.response = response;
      this.imageType = imageType;
    }

    /**
     * add the info from the image to the given frame
     * 
     * @param frame
     *          - the openCV frame
     * @param image
     *          - the rcdukes image
     */
    public void addImageInfo(Mat frame, Image image) {
      int fontFace = Core.FONT_HERSHEY_SIMPLEX;
      int fontScale = 1;
      Scalar color = new Scalar(255, 0, 0);
      String text = String.format("%5d", image.getFrameIndex());
      Point pos = new Point(frame.width() - 100, 25);
      Imgproc.putText(frame, text, pos, fontFace, fontScale, color);
    }

    /**
     * serve the next image
     */
    public void streamNextImage() {
      String contentType = contentTypes[imageFormat.ordinal()];
      response.write("Content-Type: " + contentType + crlf);
      response.write(crlf);
      // @FIXME - still too hacky - need an Observable here
      ImageCollector imageCollector = Detector.getImageCollector();
      Image image = imageCollector.getImage(imageType, true);
      Mat frame = image.getFrame().clone();
      addImageInfo(frame, image);
      byte[] bytes = ImageUtils.mat2ImageBytes(frame,
          exts[imageFormat.ordinal()]);
      Buffer data = Buffer.buffer().appendBytes(bytes);
      response.write(data);
      // boundary
      response.write(crlf);
      response.write("--" + boundary + crlf);

      // 1 image per second
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // ...
      }
    }

    public void preamble() {
      // initiate a multipart response
      response.setChunked(true); // ! important

      String contentType = "multipart/x-mixed-replace; boundary=" + boundary;
      response.putHeader("content-type", contentType);

      response.write(crlf);
      response.write("--" + boundary + crlf);
    }

    @Override
    public void run() {
      preamble();
      running = true;
      while (running) {
        this.streamNextImage();
      }
    }
  }
}