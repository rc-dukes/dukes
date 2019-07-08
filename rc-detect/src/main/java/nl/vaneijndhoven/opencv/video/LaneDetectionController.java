package nl.vaneijndhoven.opencv.video;

import org.opencv.core.Mat;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import nl.vaneijndhoven.dukes.common.Config;
import nl.vaneijndhoven.opencv.edgedectection.CannyEdgeDetector;
import nl.vaneijndhoven.opencv.linedetection.ProbabilisticHoughLinesLineDetector;
import nl.vaneijndhoven.opencv.tools.ImageCollector;

/**
 * lane detection controller
 *
 */
public class LaneDetectionController {

  protected static final Logger LOG = LoggerFactory
      .getLogger(LaneDetectionController.class);

  public static double DEFAULT_LANE_BOUNDARY_ANGLE = 45;
  public static double DEFAULT_LANE_BOUNDARY_TOLERANCE = 30;

  public static final double DEFAULT_CANNY_THRESHOLD_1 = 131d;
  public static final double DEFAULT_CANNY_THRESHOLD_2 = 397d;
  public static final int DEFAULT_LINE_DETECT_RHO = 1;
  public static final double DEFAULT_LINE_DETECT_THETA = Math.PI / 180;
  public static final double DEFAULT_LINE_DETECT_THRESHOLD = 42d;
  public static final double DEFAULT_LINE_DETECT_MIN_LINE_LENGTH = 0d;
  public static final double DEFAULT_LINE_DETECT_MAX_LINE_GAP = 98d;

  private static final long MAX_DURATION_NO_LINES_DETECTED = 1000;

  private long COMMAND_LOOP_INTERVAL = 100L;
  private boolean stoppingZoneDetected = false;
  private Vertx vertx;
  private long tsLastLinesDetected = System.currentTimeMillis();

  private double previousAngle = 0;

  private double minDistanceToStoppingZone = Double.MAX_VALUE;
  private long tsLastCommand = System.currentTimeMillis();
  private long tsLastConnectionOKmessageSent = System.currentTimeMillis();
  private double lastRudderPercentageSent = 0d;

  private boolean emergencyStopActivated = false;

  public LaneDetectionController(Vertx vertx) {
    this.vertx = vertx;
    initDefaults();
  }

  private void initDefaults() {
    minDistanceToStoppingZone = Double.MAX_VALUE;
    tsLastCommand = System.currentTimeMillis();
    lastRudderPercentageSent = 0d;
  }

  public void performLaneDetection(Mat originalImage,
      CannyEdgeDetector.Config cannyConfig,
      ProbabilisticHoughLinesLineDetector.Config lineDetectorConfig,
      ImageCollector collector) {
    // ImageLaneDetection laneDetect = new ImageLaneDetection(cannyConfig,
    // lineDetectorConfig, matrix);
    // Map<String, Object> laneDetectResult =
    // laneDetect.detectLane(originalImage, collector);
    //
    // double distanceToStoppingZoneStart = (double)
    // laneDetectResult.get("distanceToStoppingZone");
    // double distanceToStoppingZoneEnd = (double)
    // laneDetectResult.get("distanceToStoppingZoneEnd");
    // double angle = (double) laneDetectResult.get("angle");
    //
    // processLane(angle, distanceToStoppingZoneStart,
    // distanceToStoppingZoneEnd);
  }

  private void processLane(double angle, double distanceToStoppingZoneStart,
      double distanceToStoppingZoneEnd) {
    long currentTime = System.currentTimeMillis();

    logConnectionOK();
    detectStoppingZone(distanceToStoppingZoneStart);
    verifyAngleFound(angle, currentTime);

    double rudderPercentage = 100 * (Math.abs(angle) / 60) * 0.45; // 0.5 voor
                                                                   // rechtdoor
                                                                   // rijden

    // double damp = Math.abs(previousAngle) - angle);

    // System.out.println("prev: " + previousAngle + ", angle: " + angle + ",
    // rudderPercentage: " + rudderPercentage + ", damp: " + damp);
    String direction="?";
    if (currentTime - tsLastCommand > COMMAND_LOOP_INTERVAL) {
   
      if ((previousAngle != 0)
          && (!((previousAngle < 0 && angle > 0)
              || (previousAngle > 0 && angle < 0)))
          && (Math.abs(angle) < Math.abs(previousAngle))) {
        direction=Config.POSITION_CENTER;
        rudderPercentage = 0;
      } else {
        if (rudderPercentage > 0) {
        
          if (angle > 0) {
            direction = Config.POSITION_LEFT;
            rudderPercentage = -rudderPercentage * 1;
          } else {
            direction = Config.POSITION_RIGHT;
            rudderPercentage = rudderPercentage * 1.3;
          }
          
        }
      }
      String msg = String.format("steering %s, percentage: %3.1f %% ",
          direction, rudderPercentage);
      LOG.debug(msg);
      
      eventBusSendAfterMS(0, "setwheel:" + rudderPercentage);
      tsLastCommand = currentTime;
      lastRudderPercentageSent = rudderPercentage;
      previousAngle = angle;

    }
  }

  private void verifyAngleFound(double angle, long currentTime) {
    if (!(angle > 0) && !(angle < 0)) {
      // no angle detected
      if ((currentTime - tsLastLinesDetected > MAX_DURATION_NO_LINES_DETECTED)
          && !emergencyStopActivated) {
        System.out.println("no angle found for "
            + MAX_DURATION_NO_LINES_DETECTED + "ms, emergency stop");
        if (vertx != null) {
          emergencyStopActivated = true;
          vertx.eventBus().send("control", "speed:stop");
        }
      }
    } else {
      // all good
      tsLastLinesDetected = currentTime;
    }
  }

  private void detectStoppingZone(double distanceToStoppingZoneStart) {
    // System.out.println("distanceToStoppingZoneStart: " +
    // distanceToStoppingZoneStart);

    if (distanceToStoppingZoneStart > 0) {

      if (distanceToStoppingZoneStart < minDistanceToStoppingZone) {
        minDistanceToStoppingZone = distanceToStoppingZoneStart;
        System.out.println("new minimal distance to stopping zone: "
            + minDistanceToStoppingZone);
      }

      if (minDistanceToStoppingZone < 100) {
        if (distanceToStoppingZoneStart - minDistanceToStoppingZone > 30) {
          if (!stoppingZoneDetected) {
            System.out.println("--- stop ---");
            eventBusSendAfterMS(1000, "speed:brake");
            stoppingZoneDetected = true;
          }
        }
      }

      // if (distanceToStoppingZoneStart - minDistanceToStoppingZone > 20) {
      // System.out.println("--- stop ---");
      // eventBusSendAfterMS(10,"speed:brake");
      // }

      // System.out.println("distanceToStoppingZoneStart: " +
      // distanceToStoppingZoneStart);
      //
      // if (distanceToStoppingZoneStart > 5 && distanceToStoppingZoneStart <
      // 15) {
      // System.out.println("--- stop ---");
      // eventBusSendAfterMS(10,"speed:brake");
      // }

      // if (distanceToStoppingZoneStart < minDistanceToStoppingZone) {
      // minDistanceToStoppingZone = distanceToStoppingZoneStart;
      // System.out.println("new minimal distance to stopping zone: " +
      // minDistanceToStoppingZone);
      // }
      //
      // if (Math.abs(distanceToStoppingZoneStart - minDistanceToStoppingZone) >
      // 10) {
      // System.out.println("distance increasing: min=" +
      // minDistanceToStoppingZone + ", cur = " + distanceToStoppingZoneStart);
      //
      // int wait = 500;
      // stoppingZoneDetected = true;
      // System.out.println("stopping zone detected, stopping in " + wait + "
      // ms");
      // // eventBusSendAfterMS(wait,"speed:brake");
      // }
    }
  }

  private void eventBusSendAfterMS(long ms, String command) {
    new java.util.Timer().schedule(new java.util.TimerTask() {
      @Override
      public void run() {
        // System.out.println("Sending command '" + command + "'.");
        if (vertx != null) {
          vertx.eventBus().send("control", command);
        } else {
          System.out.println(
              "Couldn't send command '" + command + "', Vert.x not inited");
        }
      }
    }, ms);
  }

  private void logConnectionOK() {
    int logConnectionOKinterval = 1000;

    long currentTime = System.currentTimeMillis();
    if (currentTime - tsLastConnectionOKmessageSent > logConnectionOKinterval) {
      eventBusSendAfterMS(0, "log:LaneDetectionController connected");
      tsLastConnectionOKmessageSent = currentTime;
    }

  }

}
