package nl.vaneijndhoven.dukes.app;

import static nl.vaneijndhoven.dukes.app.LaneDetectionController.DEFAULT_CANNY_THRESHOLD_1;
import static nl.vaneijndhoven.dukes.app.LaneDetectionController.DEFAULT_CANNY_THRESHOLD_2;
import static nl.vaneijndhoven.dukes.app.LaneDetectionController.DEFAULT_LINE_DETECT_MAX_LINE_GAP;
import static nl.vaneijndhoven.dukes.app.LaneDetectionController.DEFAULT_LINE_DETECT_MIN_LINE_LENGTH;
import static nl.vaneijndhoven.dukes.app.LaneDetectionController.DEFAULT_LINE_DETECT_RHO;
import static nl.vaneijndhoven.dukes.app.LaneDetectionController.DEFAULT_LINE_DETECT_THETA;
import static nl.vaneijndhoven.dukes.app.LaneDetectionController.DEFAULT_LINE_DETECT_THRESHOLD;
import static nl.vaneijndhoven.opencv.tools.MemoryManagement.closable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import nl.vaneijndhoven.dukes.common.Config;
import nl.vaneijndhoven.opencv.edgedectection.CannyEdgeDetector;
import nl.vaneijndhoven.opencv.linedetection.ProbabilisticHoughLinesLineDetector;
import nl.vaneijndhoven.opencv.tools.ImageCollector;
import nl.vaneijndhoven.opencv.tools.MemoryManagement;

public class LaneDetectionGUI extends BaseGUI {
  @FXML
  private Slider cannyThreshold1;
  @FXML
  private Slider cannyThreshold2;
  @FXML
  private Slider lineDetectRho;
  @FXML
  private Slider lineDetectTheta;
  @FXML
  private Slider lineDetectThreshold;
  @FXML
  private Slider lineDetectMinLineLength;
  @FXML
  private Slider lineDetectMaxLineGap;

  private Vertx vertx;
  private ScheduledExecutorService timer;
  private VideoCapture capture = new VideoCapture();
  private boolean cameraActive;

  private CannyEdgeDetector.Config cannyConfig = new CannyEdgeDetector.Config();
  private ProbabilisticHoughLinesLineDetector.Config houghLinesConfig = new ProbabilisticHoughLinesLineDetector.Config();
  private LaneDetectionController controller = null;

  private boolean configured = false;

  public LaneDetectionGUI() {
    Config.configureLogging();
    initVertx();
  }

  public void startCamera() throws Exception {
    configureGUI();

    // on start, reset default values in controller
    controller = new LaneDetectionController(vertx);

    if (!this.cameraActive) {
      // open configured video file
      this.capture.open(displayer.getLaneVideoProperty().getValue());

      if (this.capture.isOpened()) {
        this.cameraActive = true;

        Runnable frameGrabber = () -> {
          try {
            displayCurrentSliderValues();
            applySliderValuesToConfig();

            try (MemoryManagement.ClosableMat<Mat> originalImage = closable(
                grabFrame())) {
              displayer.displayOriginal(originalImage.get());

              ImageCollector collector = new ImageCollector();
              controller.performLaneDetection(originalImage.get(), cannyConfig,
                  houghLinesConfig, collector);
              displayer.display1(collector.edges());
              displayer.display2(collector.lines());
            }
          } catch (Exception e) {
            System.out.println("Exception detected: ");
            e.printStackTrace();
          }
        };

        this.timer = Executors.newSingleThreadScheduledExecutor();
        // this.timer = Executors.newScheduledThreadPool(100);
        this.timer.scheduleAtFixedRate(frameGrabber, 0, 50,
            TimeUnit.MILLISECONDS);

        displayer.setCameraButtonText("Stop Camera");
      } else {
        System.err.println("Failed to open the camera connection...");
      }
    } else {
      this.cameraActive = false;
      displayer.setCameraButtonText("Start Camera");

      try {
        this.timer.shutdown();
        this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        System.err.println(
            "Exception in stopping the frame capture, trying to release the camera now... "
                + e);
      }

      this.capture.release();
    }
  }

  private void configureGUI() {
    if (!configured) {
      configureSliderDefaults();
      configured = true;
    }
  }

  private void applySliderValuesToConfig() {
    cannyConfig.setThreshold1(cannyThreshold1.getValue());
    cannyConfig.setThreshold2(cannyThreshold2.getValue());
    houghLinesConfig.setRho(lineDetectRho.getValue());
    houghLinesConfig.setTheta(lineDetectTheta.getValue());
    houghLinesConfig
        .setThreshold(new Double(lineDetectThreshold.getValue()).intValue());
    houghLinesConfig.setMinLineLength(lineDetectMinLineLength.getValue());
    houghLinesConfig.setMaxLineGap(lineDetectMaxLineGap.getValue());
  }

  private void displayCurrentSliderValues() {
    // show the current selected HSV range
    String valuesToPrint = "canny1: " + cannyThreshold1.getValue()
        + ", canny2: " + cannyThreshold2.getValue() + ", rho: "
        + lineDetectRho.getValue() + ", theta: " + lineDetectTheta.getValue()
        + ", threshold: " + lineDetectThreshold.getValue() + ", minLength: "
        + lineDetectMinLineLength.getValue() + ", maxGap: "
        + lineDetectMaxLineGap.getValue();
    displayer.showCurrentValues(valuesToPrint);
  }

  private void configureSliderDefaults() {
    this.cannyThreshold1.setValue(DEFAULT_CANNY_THRESHOLD_1);
    this.cannyThreshold2.setValue(DEFAULT_CANNY_THRESHOLD_2);
    this.lineDetectRho.setValue(DEFAULT_LINE_DETECT_RHO);
    this.lineDetectTheta.setValue(DEFAULT_LINE_DETECT_THETA);
    this.lineDetectThreshold.setValue(DEFAULT_LINE_DETECT_THRESHOLD);
    this.lineDetectMinLineLength.setValue(DEFAULT_LINE_DETECT_MIN_LINE_LENGTH);
    this.lineDetectMaxLineGap.setValue(DEFAULT_LINE_DETECT_MAX_LINE_GAP);
  }

  private Mat grabFrame() {
    final Mat frame = new Mat();
    if (this.capture.isOpened()) {
      try {
        this.capture.read(frame);

        if (!frame.empty()) {
          return frame;
        }

      } catch (Exception e) {
        System.err.print("ERROR");
        e.printStackTrace();
      }
    }
    return frame;
  }

  private void initVertx() {
    VertxOptions options = new VertxOptions().setClustered(true)
        .setClusterManager(Config.createHazelcastConfig());

    Vertx.clusteredVertx(options, resultHandler -> {
      Vertx vertx = resultHandler.result();
      this.vertx = vertx;
      controller = new LaneDetectionController(vertx);

      Runtime.getRuntime().addShutdownHook(new Thread() {
        public void run() {
          vertx.close();
        }
      });
    });
  }
}
