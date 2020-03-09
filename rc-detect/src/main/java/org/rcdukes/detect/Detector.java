package org.rcdukes.detect;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.rcdukes.camera.CameraMatrix;
import org.rcdukes.common.Characters;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.Events;
import org.rcdukes.detect.linedetection.HoughLinesLineDetector;
import org.rcdukes.detectors.EdgeDetector;
import org.rcdukes.detectors.LineDetector;
import org.rcdukes.geometry.LaneDetectionResult;
import org.rcdukes.opencv.NativeLibrary;
import org.rcdukes.video.ImageCollector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;
import nl.vaneijndhoven.opencv.edgedectection.CannyEdgeDetector;

/**
 * Detector aka Daisy
 */
public class Detector extends DukesVerticle {

  // @FIXME - does not belong here ...
  private final static long START_LIGHT_DETECTION_INTERVAL = 50;
  private static ImageCollector currentCollector = new ImageCollector();

  public static synchronized ImageCollector getImageCollector() {
    return currentCollector;
  }

 
  private ImageFetcher fetcher;
  private Disposable startLaneDetectionSubscription;
  private Disposable startLightDetectionSubscription;

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
    // register vert.x event handlers
    consumer(Events.START_LANE_DETECTION,
        this::startLaneDetectionMessageHandler);
    // consumer(Events.START_STARTLIGHT_DETECTION, this::startSLD); - blocking issue ..
    consumer(Events.CAMERA_CONFIG_UPDATE, this::cameraConfig);
    consumer(Events.CANNY_CONFIG_UPDATE, this::cannyConfig);
    consumer(Events.HOUGH_CONFIG_UPDATE, this::houghConfig);
    consumer(Events.CAMERA_MATRIX_UPDATE, this::cameraMatrix);
    super.postStart();
  }

  /**
   * start Lane Detection
   * 
   * @param message
   */
  private void startLaneDetectionMessageHandler(Message<JsonObject> message) {
    JsonObject jo = message.body();
    CameraConfig cameraConfig = super.fromJsonObject(jo, CameraConfig.class);
    // is this a restart?
    if (startLaneDetectionSubscription != null)
      startLaneDetectionSubscription.dispose();;
    if (fetcher!=null)
      fetcher.close();
    startLaneDetectionSubscription = startLaneDetection(cameraConfig)
        // .doOnNext(detection -> LOG.trace("Image lane detection processing
        // result: " + detection))
        .subscribe(
            lane -> vertx.eventBus().publish(Events.LANE_DETECTED.name(), lane),
            error -> LOG.error("Error during lane detection image processing:",
                error),
            () -> LOG.info("Lane detection image processing ended"));
  }

  private void startSLD(Message<JsonObject> message) {
    // is this a restart?
    if (startLightDetectionSubscription != null)
      startLightDetectionSubscription.dispose();;
    startLightDetectionSubscription = startStartLightDetection(message)
        // .doOnNext(detection -> LOG.trace("Image start light processing
        // result: " + detection))
        // .takeWhile()
        .subscribe(
            light -> vertx.eventBus()
                .publish(Events.START_STARTLIGHT_DETECTION.name(), light),
            error -> LOG.error("Error during start light image processing:",
                error),
            () -> LOG.info("Start light image processing ended"));
  }

  private void cameraConfig(Message<JsonObject> message) {
    JsonObject jo = message.body();
    JsonObject currentJo = this.getSharedData("cameraConfig");
    boolean restart = false;
    if (currentJo == null) {
      restart = true;
    } else {
      CameraConfig currentCameraConfig = currentJo.mapTo(CameraConfig.class);
      CameraConfig cameraConfig = jo.mapTo(CameraConfig.class);
      restart = !cameraConfig.getSource()
          .equals(currentCameraConfig.getSource());
    }
    // update the camera Configuration
    shareData("cameraConfig", jo);
    if (restart) {
      sendEvent(this.character,Events.START_LANE_DETECTION, jo);
      sendEvent(this.character,Events.START_STARTLIGHT_DETECTION, jo);
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
  private Observable<LaneDetectionResult> startLaneDetection(CameraConfig cameraConfig) {
    return startLaneDetectionObservable(cameraConfig).map(ldr -> ldr);
  }

  /**
   * startLaneDetectionObservable
   * 
   * @param cameraConfig
   * @return
   */
  private Observable<LaneDetectionResult> startLaneDetectionObservable(
      CameraConfig cameraConfig) {
    fetcher = new ImageFetcher(cameraConfig.getSource());
    fetcher.setFps(cameraConfig.getFps());

    LOG.info(
        "Started image processing for source: " + cameraConfig.getSource());
    return fetcher.toObservable().subscribeOn(Schedulers.newThread())
        .throttleFirst(cameraConfig.getInterval(), TimeUnit.MILLISECONDS)
        .map(image -> {
          currentCollector = new ImageCollector();
          EdgeDetector edgeDetector = super.getSharedPojo("canny",
              CannyEdgeDetector.class);
          LineDetector lineDetector = super.getSharedPojo("hough",
              HoughLinesLineDetector.class);
          CameraConfig updatedCameraConfig = super.getSharedPojo("cameraConfig",
              CameraConfig.class);
          LaneDetectionResult detection = new LaneDetector(edgeDetector,
              lineDetector, updatedCameraConfig, createMatrix(),
              currentCollector).detect(image);
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

    // @FIXME - don't create multiple image fetchers
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
