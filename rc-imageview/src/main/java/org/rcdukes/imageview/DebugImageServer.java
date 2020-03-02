package org.rcdukes.imageview;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.opencv.core.Mat;
import org.rcdukes.common.Characters;
import org.rcdukes.common.Config;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.Environment;
import org.rcdukes.common.Events;
import org.rcdukes.detect.Detector;
import org.rcdukes.error.ErrorHandler;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageCollector;
import org.rcdukes.video.ImageCollector.ImageType;
import org.rcdukes.video.ImageUtils;

import io.vertx.core.Future;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;

/**
 * Imageview verticle (Rosco)
 *
 */
public class DebugImageServer extends DukesVerticle {
  public static boolean SERVE_TEST_IMAGES=false;

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

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    super.preStart();
    // @TODO - get config information from config Verticle (or shared data ...)
    File mediaPath=new File(Environment.dukesHome+"media");
    mediaPath.mkdirs();
    VideoRecorder.MEDIA_PATH=mediaPath.getPath();
    int port = Config.getEnvironment().getInteger(Config.IMAGEVIEW_PORT);
    server = vertx.createHttpServer().requestHandler(this::sendImage);
    // Now bind the server:
    server.listen(port, res -> {
      if (res.succeeded()) {
        startFuture.complete();
        consumer(Events.START_RECORDING, x -> startRecording());
        consumer(Events.STOP_RECORDING, x -> stopRecording());
        consumer(Events.SIMULATOR_IMAGE, this::receiveSimulatorImage);
        consumer(Events.PHOTO_SHOOT,x->shootPhoto());
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
  
  /**
   * receive an image from the simulator
   */
  protected void receiveSimulatorImage(Message<String> message) {
    String imgData = message.body(); // in DataURL format ...
    try {
      Mat imageMat=ImageUtils.matFromDataUrl(imgData, "jpg");
      ImageCollector imageCollector = Detector.getImageCollector();
      imageCollector.addImage(imageMat, ImageType.simulator);
    } catch (IOException e) {
      ErrorHandler.getInstance().handle(e);
    }
  }
  
  /**
   * // @FIXME - still too hacky - need an Observable here
   * @param imageType
   * @return
   */
  public Image getNextImage(ImageType imageType) {
    ImageCollector imageCollector = Detector.getImageCollector();
    Image image = imageCollector.getImage(imageType, SERVE_TEST_IMAGES);
    return image;
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
   * create a single shot video
   */
  protected void shootPhoto() {
    ImageCollector imageCollector = Detector.getImageCollector();
    for (ImageType imageType : ImageType.values()) {
      String filepath=VideoRecorder.filePath(imageType.name(), ".jpg");
      Image image=imageCollector.getImage(imageType, false);
      if (image!=null)
        ImageUtils.writeImageToFilepath(image.getFrame(), filepath);
    }
  }

  /**
   * stop Recording
   */
  protected void stopRecording() {
    Iterator<Entry<ImageType, VideoRecorder>> it = recorders.entrySet().iterator();
    while (it.hasNext())
    {
       Entry<ImageType, VideoRecorder> entry= it.next();
       VideoRecorder recorder = entry.getValue();
       recorder.stop();
       it.remove();
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
    Mat mat = null;
    Image image=this.getNextImage(imageType);
    if (image != null) {
      mat = image.getFrame();
      this.sendImageBytes(request, image.getImageBytes());
    }
    // optionally record
    recordFrame(mat,imageType);
  }
  
  /**
   * record the given frame if the videorecorder for it's imageType is active
   * @param frame
   * @param imageType
   */
  public void recordFrame(Mat frame,ImageType imageType) {
    if (recorders.containsKey(imageType) && frame != null) {
      VideoRecorder recorder = recorders.get(imageType);
      recorder.recordMat(frame);
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
    Runnable multipartStreamer = new MultipartStreamer(this, response, imageType);
    Thread streamThread = new Thread(multipartStreamer);
    streamThread.start();
  }
  
}