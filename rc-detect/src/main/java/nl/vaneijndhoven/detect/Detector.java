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
import nl.vaneijndhoven.opencv.linedetection.ProbabilisticHoughLinesLineDetector;
import nl.vaneijndhoven.opencv.tools.ImageCollector;
import rx.Observable;

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
    vertx.eventBus().send(
        Characters.DAISY.getCallsign() + ":" + START_LANE_DETECTION,
        message.body());
    vertx.eventBus().send(
        Characters.DAISY.getCallsign() + ":" + START_STARTLIGHT_DETECTION,
        message.body());
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
    vertx.sharedData().getLocalMap(Characters.DAISY.name()).put("canny",
        message.body());
  }

  private void houghConfig(Message<JsonObject> message) {
    vertx.sharedData().getLocalMap(Characters.DAISY.name()).put("hough",
        message.body());
  }

  private void cameraMatrix(Message<JsonObject> message) {
    vertx.sharedData().getLocalMap(Characters.DAISY.name()).put("matrix",
        message.body());
  }

  private long getInterval() {
    Long interval = (Long) vertx.sharedData()
        .getLocalMap(Characters.DAISY.name()).get("interval");
    return interval != null ? interval : LANE_DETECTION_INTERVAL;
  }

  private CannyEdgeDetector.Config createCanny() {
    CannyEdgeDetector.Config canny = new CannyEdgeDetector.Config();
    JsonObject cannyConfig = (JsonObject) vertx.sharedData()
        .getLocalMap(Characters.DAISY.name()).get("canny");

    if (cannyConfig != null) {
      canny.setThreshold1(cannyConfig.getDouble("threshold1"));
      canny.setThreshold2(cannyConfig.getDouble("threshold2"));
    }

    return canny;
  }

  private ProbabilisticHoughLinesLineDetector.Config createHoughLines() {
    ProbabilisticHoughLinesLineDetector.Config hough = new ProbabilisticHoughLinesLineDetector.Config();
    JsonObject houghConfig = (JsonObject) vertx.sharedData()
        .getLocalMap(Characters.DAISY.name()).get("hough");

    if (houghConfig != null) {
      hough = new ProbabilisticHoughLinesLineDetector.Config();
      hough.setRho(houghConfig.getDouble("rho"));
      hough.setTheta(houghConfig.getDouble("theta"));
      hough.setThreshold(houghConfig.getInteger("threshold"));
      hough.setMaxLineGap(houghConfig.getDouble("maxLineGap"));
      hough.setMinLineLength(houghConfig.getDouble("minLineLength"));
    }

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

  private Observable<String> startLaneDetection(Message<JsonObject> msg) {
    JsonObject jo = msg.body();
    JsonObject config = jo.getJsonObject("config");

    CannyEdgeDetector.Config canny = new CannyEdgeDetector.Config();
    ProbabilisticHoughLinesLineDetector.Config hough = new ProbabilisticHoughLinesLineDetector.Config();
    long interval = LANE_DETECTION_INTERVAL;
    if (config != null) {
      JsonObject cannyCfg = config.getJsonObject("canny");
      if (cannyCfg != null) {
        canny = new CannyEdgeDetector.Config();
        canny.setThreshold1(cannyCfg.getDouble("threshold1"));
        canny.setThreshold2(cannyCfg.getDouble("threshold2"));
      }

      JsonObject houghCfg = config.getJsonObject("hough");
      if (houghCfg != null) {
        hough = new ProbabilisticHoughLinesLineDetector.Config();
        hough.setRho(houghCfg.getDouble("rho"));
        hough.setTheta(houghCfg.getDouble("theta"));
        hough.setThreshold(houghCfg.getInteger("threshold"));
        hough.setMaxLineGap(houghCfg.getDouble("maxLineGap"));
        hough.setMinLineLength(houghCfg.getDouble("minLineLength"));
      }

      if (config.containsKey("interval")) {
        interval = config.getLong("interval");
      }
    }

    return startLaneDetection(jo.getString("source"), interval).map(map -> {
      try {
        return new ObjectMapper().writeValueAsString(map);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private Observable<Object> startLaneDetection(String source, long interval) {
    ImageFetcher fetcher = new ImageFetcher(source);

    LOG.info("Started image processing for source: " + source);
    return fetcher.toObservable()
        // @TODO make sample interval configurable (again?)
        // .sample(interval, TimeUnit.MILLISECONDS)
        .doOnNext(frame -> Detector.MAT = frame).map(frame -> {
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
