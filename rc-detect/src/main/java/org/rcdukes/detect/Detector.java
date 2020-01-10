package org.rcdukes.detect;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.rcdukes.camera.CameraMatrix;
import org.rcdukes.opencv.NativeLibrary;
import org.rcdukes.video.ImageCollector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;
import org.rcdukes.common.Characters;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.Events;
import nl.vaneijndhoven.opencv.edgedectection.CannyEdgeDetector;
import nl.vaneijndhoven.opencv.linedetection.HoughLinesLineDetector;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Detector aka Daisy
 */
public class Detector extends DukesVerticle {

  // @FIXME - does not belong here ...
  private final static long START_LIGHT_DETECTION_INTERVAL = 50;
  public static Mat camera = null;
  public static Mat MAT = null;
  public static Mat BIRDS_EYE = null;
  public static byte[] CANNY_IMG = null;

  /**
   * default constructor
   */
  public Detector() {
    super(Characters.DAISY);
  }

  @Override
  public void start() throws Exception {
    super.preStart();
    NativeLibrary.load();
    consumer(Events.CAMERA_CONFIG_UPDATE, this::cameraConfig);
    consumer(Events.START_LANE_DETECTION,this::startLD);
    // vertx.eventBus().consumer(Characters.DAISY.getCallsign() + ":" +
    // START_STARTLIGHT_DETECTION, this::startSLD);
    consumer(Events.CANNY_CONFIG_UPDATE, this::cannyConfig);
    consumer(Events.HOUGH_CONFIG_UPDATE,this::houghConfig);
    consumer(Events.CAMERA_MATRIX_UPDATE,this::cameraMatrix);

    super.postStart();
  }

  private void startLD(Message<JsonObject> message) {
    startLaneDetection(message)
        // .doOnNext(detection -> LOG.trace("Image lane detection processing
        // result: " + detection))
        .subscribe(
            lane -> vertx.eventBus().publish(Events.START_LANE_DETECTION.name(), lane),
            error -> LOG.error("Error during lane detection image processing:",
                error),
            () -> LOG.info("Lane detection image processing ended"));
  }

  private void startSLD(Message<JsonObject> message) {
    startStartLightDetection(message)
        // .doOnNext(detection -> LOG.trace("Image start light processing
        // result: " + detection))
        // .takeWhile()
        .subscribe(
            light -> vertx.eventBus().publish(Events.START_STARTLIGHT_DETECTION.name(),
                light),
            error -> LOG.error("Error during start light image processing:",
                error),
            () -> LOG.info("Start light image processing ended"));
  }

  private void cameraConfig(Message<JsonObject> message) {
    JsonObject jo = message.body();
    JsonObject currentJo = this.getSharedData("cameraConfig");
    boolean start = false;
    boolean restart = false;
    if (currentJo == null) {
      start = true;
    } else {
      CameraConfig currentCameraConfig = currentJo.mapTo(CameraConfig.class);
      CameraConfig cameraConfig = jo.mapTo(CameraConfig.class);
      restart = !cameraConfig.getSource()
          .equals(currentCameraConfig.getSource());
    }
    shareData("cameraConfig", jo);
    if (restart) {
      // @FIXME - STOP the exiting detections
    }
    if (start) {
      sendEvent(Events.START_LANE_DETECTION, jo);
      sendEvent(Events.START_STARTLIGHT_DETECTION, jo);
    }
  }

  private void cannyConfig(Message<JsonObject> message) {
    shareData("canny", message.body());
  }

  private void houghConfig(Message<JsonObject> message) {
    shareData("hough", message.body());
  }

  private void cameraMatrix(Message<JsonObject> message) {
    shareData("matrix", message.body());
  }

  private CameraConfig createCameraConfig() {
    JsonObject jo = getSharedData("cameraConfig");
    CameraConfig cameraConfig;
    if (jo == null)
      cameraConfig = new CameraConfig();
    else
      cameraConfig = jo.mapTo(CameraConfig.class);
    return cameraConfig;
  }

  private CannyEdgeDetector createCanny() {
    JsonObject jo = getSharedData("canny");
    CannyEdgeDetector canny;
    if (jo == null)
      canny = new CannyEdgeDetector();
    else
      canny = jo.mapTo(CannyEdgeDetector.class);
    return canny;
  }

  private HoughLinesLineDetector createHoughLines() {
    JsonObject jo = getSharedData("hough");

    HoughLinesLineDetector hough;
    if (jo == null)
      hough = new HoughLinesLineDetector();
    else
      hough = jo.mapTo(HoughLinesLineDetector.class);
    return hough;
  }

  private CameraMatrix createMatrix() {
    String matrix = (String) vertx.sharedData()
        .getLocalMap(Characters.DAISY.name()).get("matrix");

    return matrix != null
        ? CameraMatrix.deserizalize((String) vertx.sharedData()
            .getLocalMap(Characters.DAISY.name()).get("matrix"))
        : CameraMatrix.DEFAULT;
  }

  /**
   * start the lane detection based on the given message
   * 
   * @param msg
   * @return
   **/
  private Observable<String> startLaneDetection(Message<JsonObject> msg) {
    JsonObject jo = msg.body();
    // @TODO - check if this is ever transmitted ...
    if (jo.containsKey("config")) {
      JsonObject config = jo.getJsonObject("config");
      if (config.containsKey("cameraConfig")) {
        shareData("cameraConfig", config.getJsonObject("cameraConfig"));
      }
      if (config.containsKey("canny"))
        shareData("canny", config.getJsonObject("canny"));
      if (config.containsKey("hough"))
        shareData("hough", config.getJsonObject("hough"));
    }
    CameraConfig cameraConfig = createCameraConfig();
    return startLaneDetection(cameraConfig.getSource(),
        cameraConfig.getInterval()).map(map -> {
          try {
            return new ObjectMapper().writeValueAsString(map);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        });
  }

  /**
   * start lane detection
   * 
   * @param source
   * @param interval
   * @return an observable
   */
  private Observable<Object> startLaneDetection(String source, long interval) {
    ImageFetcher fetcher = new ImageFetcher(source);

    LOG.info("Started image processing for source: " + source);
    return fetcher.toObservable().subscribeOn(Schedulers.newThread())
        .throttleFirst(interval, TimeUnit.MILLISECONDS).doOnNext(image -> {
          Mat frame = image.getFrame();
          Detector.MAT = frame;
          Detector.camera = frame.clone();
        }).map(image -> {
          ImageCollector collector = new ImageCollector();
          Map<String, Object> detection = new LaneDetector(
              createCanny().withImageCollector(collector),
              createHoughLines().withImageCollector(collector),
              createCameraConfig(), createMatrix(), collector).detect(image);
          Detector.CANNY_IMG = collector.edges();
          return detection;
        });
  }

  private Observable startStartLightDetection(Message<JsonObject> msg) {
    JsonObject jo = msg.body();
    JsonObject config = jo.getJsonObject("config");

    long interval = START_LIGHT_DETECTION_INTERVAL;
    if (config != null) {
      if (config.containsKey("interval")) {
        interval = config.getLong("interval");
      }
    }

    StartLightDetectorImpl.Config config1 = new StartLightDetectorImpl.Config();
    StartLightDetectorImpl startLightDetector = new StartLightDetectorImpl(
        config1);

    ImageFetcher fetcher = new ImageFetcher(jo.getString("source"));

    return fetcher.toObservable().sample(interval, TimeUnit.MILLISECONDS)
        .map(startLightDetector::detect).map(map -> {
          try {
            return new ObjectMapper().writeValueAsString(map);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        });
  }

}
