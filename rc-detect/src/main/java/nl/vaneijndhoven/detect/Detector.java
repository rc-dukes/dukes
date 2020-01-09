package nl.vaneijndhoven.detect;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;

import com.bitplan.opencv.NativeLibrary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;
import nl.vaneijndhoven.dukes.camera.matrix.CameraMatrix;
import nl.vaneijndhoven.dukes.common.Characters;
import nl.vaneijndhoven.dukes.common.DukesVerticle;
import nl.vaneijndhoven.dukes.common.Events;
import nl.vaneijndhoven.opencv.edgedectection.CannyEdgeDetector;
import nl.vaneijndhoven.opencv.linedetection.HoughLinesLineDetector;
import nl.vaneijndhoven.opencv.video.ImageCollector;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Detector aka Daisy
 */
public class Detector extends DukesVerticle {

  private final static long LANE_DETECTION_INTERVAL = 200;
  private final static long START_LIGHT_DETECTION_INTERVAL = 50;

  private final static String START_LANE_DETECTION = "START_LANE_DETECTION";
  private final static String START_STARTLIGHT_DETECTION = "START_STARTLIGHT_DETECTION";
  private final static String CANNY_CONFIG_UPDATE = "CANNY_CONFIG_UPDATE";
  private final static String HOUGH_CONFIG_UPDATE = "HOUGH_CONFIG_UPDATE";
  private final static String CAMERA_MATRIX_UPDATE = "CAMERA_MATRIX_UPDATE";

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
    vertx.eventBus().consumer(Events.STREAMADDED.name(), this::streamAdded);
    vertx.eventBus().consumer(
        Characters.DAISY.getCallsign() + ":" + START_LANE_DETECTION,
        this::startLD);
    // vertx.eventBus().consumer(Characters.DAISY.getCallsign() + ":" +
    // START_STARTLIGHT_DETECTION, this::startSLD);

    vertx.eventBus().consumer(
        Characters.DAISY.getCallsign() + ":" + CANNY_CONFIG_UPDATE,
        this::cannyConfig);
    vertx.eventBus().consumer(
        Characters.DAISY.getCallsign() + ":" + HOUGH_CONFIG_UPDATE,
        this::houghConfig);
    vertx.eventBus().consumer(
        Characters.DAISY.getCallsign() + ":" + CAMERA_MATRIX_UPDATE,
        this::cameraMatrix);

    super.postStart();
  }

  private void streamAdded(Message<JsonObject> message) {
    JsonObject jo = message.body();
    String json=jo.encodePrettily();
    LOG.info("streamadded received:"+json);
    vertx.eventBus()
        .send(Characters.DAISY.getCallsign() + ":" + START_LANE_DETECTION, jo);
    vertx.eventBus().send(
        Characters.DAISY.getCallsign() + ":" + START_STARTLIGHT_DETECTION, jo);
  }

  private void startLD(Message<JsonObject> message) {
    startLaneDetection(message)
        // .doOnNext(detection -> LOG.trace("Image lane detection processing
        // result: " + detection))
        .subscribe(
            lane -> vertx.eventBus().publish(Events.LANEDETECTION.name(), lane),
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
            light -> vertx.eventBus().publish(Events.STARTLIGHTDETECTION.name(),
                light),
            error -> LOG.error("Error during start light image processing:",
                error),
            () -> LOG.info("Start light image processing ended"));
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

  private long getInterval() {
    Long interval = (Long) vertx.sharedData()
        .getLocalMap(Characters.DAISY.name()).get("interval");
    return interval != null ? interval : LANE_DETECTION_INTERVAL;
  }

  private CannyEdgeDetector createCanny() {
    JsonObject jo=getSharedData("canny");
    CannyEdgeDetector canny;
    if (jo==null)
      canny=new CannyEdgeDetector();
    else
      canny=jo.mapTo(CannyEdgeDetector.class);
    return canny;
  }

  private HoughLinesLineDetector createHoughLines() {
    JsonObject jo=getSharedData("hough");
    
    HoughLinesLineDetector hough;
    if (jo==null)
      hough=new HoughLinesLineDetector();
    else
      hough=jo.mapTo(HoughLinesLineDetector.class);
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
    JsonObject config = jo.getJsonObject("config");
    shareData("canny",config.getJsonObject("canny"));
    shareData("hough",config.getJsonObject("hough"));
    long interval = LANE_DETECTION_INTERVAL;
    if (config.containsKey("interval")) {
      interval = config.getLong("interval");
    }
    return startLaneDetection(jo.getString("source"), interval).map(map -> {
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
        .throttleFirst(interval, TimeUnit.MILLISECONDS).doOnNext(frame -> {
          Detector.MAT = frame;
          Detector.camera = frame.clone();
        }).map(frame -> {
          ImageCollector collector = new ImageCollector();
          Map<String, Object> detection = new LaneDetector(createCanny(),
              createHoughLines(), createMatrix(), collector).detect(frame);
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

    StartLightDetector.Config config1 = new StartLightDetector.Config();
    StartLightDetector startLightDetector = new StartLightDetector(config1);

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
