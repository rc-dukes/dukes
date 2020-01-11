package org.rcdukes.app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.rcdukes.detectors.StartLightDetector;
import org.rcdukes.video.ImageCollector;
import org.rcdukes.video.ImageCollector.ImageType;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import org.rcdukes.common.Config;
import nl.vaneijndhoven.objects.StartLight;
import nl.vaneijndhoven.opencv.startlightdetection.DefaultStartLightDetector;

/**
 * StartLight DetectionController
 * @author wf
 *
 */
public class StartLightDetectionGUI extends BaseGUI {

  private Vertx vertx;
  private DefaultStartLightDetector.Config startLineConfig = new DefaultStartLightDetector.Config();
  private StartLightDetector detector = new DefaultStartLightDetector();

  public StartLightDetectionGUI() {
    Config.configureLogging();
    VertxOptions options = new VertxOptions().setClustered(true)
        .setClusterManager(Config.createHazelcastConfig());

    Vertx.clusteredVertx(options, resultHandler -> {
      Vertx vertx = resultHandler.result();
      this.vertx = vertx;
    });

  }

  private boolean lightOn = false;
  private int frameIndex=0;

  String lastCommandSent = "";

  private int numberOfLightOffEvents = 0;

  // FXML slider for setting HSV ranges
  @FXML
  private Slider hueStart;
  @FXML
  private Slider hueStop;
  @FXML
  private Slider saturationStart;
  @FXML
  private Slider saturationStop;
  @FXML
  private Slider valueStart;
  @FXML
  private Slider valueStop;
 
  // a timer for acquiring the video stream
  private ScheduledExecutorService timer;
  // the OpenCV object that performs the video capture
  private VideoCapture capture = new VideoCapture();
  // a flag to change the button behavior
  private boolean cameraActive;

  public void startCamera() {
    this.hueStart.adjustValue(0.0d);
    this.hueStop.adjustValue(28.3d);

    this.saturationStart.adjustValue(71.9d);
    this.saturationStop.adjustValue(98.7);

    this.valueStart.adjustValue(213.9d);
    this.valueStop.adjustValue(240.6d);

    if (!this.cameraActive) {
      // start the video capture
      this.capture.open(displayer.getStartVideoProperty().getValue());
 
      // is the video stream available?
      if (this.capture.isOpened()) {
        this.cameraActive = true;

        // grab a frame every 33 ms (30 frames/sec)
        Runnable frameGrabber = () -> {
          ImageCollector imageCollector = new ImageCollector();
          // detector.withImageCollector(imageCollector);
          StartLight startLight = detectStartLight(imageCollector);
          displayer.displayOriginal(imageCollector.getImage(ImageType.camera));
          displayer.display1(imageCollector.getImage(ImageType.mask));
          displayer.display2(imageCollector.getImage(ImageType.morph));
          // displayer.displayOriginal(imageCollector.startLight());
          if (this.vertx != null) {

            // int minimumNumberOfLightOffEvents = 2;
            if (startLight.started() && !lastCommandSent.equals("on")) {
              System.out.println("Enough light off events received, starting");
              // power on
              lastCommandSent = "on";
              eventBusSendAfterMS(10, "setspeed:3"); // in percentage
              // eventBusSendAfterMS(100, "speed:up");
              // eventBusSendAfterMS(30, "speed:up");

              // vertx-eventBus().send("control", "speed:up");
              // vertx-eventBus().send("control", "speed:up");

              Executors.newSingleThreadScheduledExecutor()
                  .schedule(() -> stopCamera(), 500, TimeUnit.MILLISECONDS);

              // vertx-eventBus().send("control", "speed:up");
              // vertx-eventBus().send("control", "speed:up");
              // vertx-eventBus().send("control", "speed:up");
              // Platform.runLater(() -> this.cameraButton.setText("Stop
              // Camera"));
            }

            if (!startLight.started() && !lastCommandSent.equals("off")) {
              // power off
              System.out.println("light is on, stopping");
              lastCommandSent = "off";
              // eventBusSendAfterMS(10, "speed:stop");

            }

          }

        };

        this.timer = Executors.newSingleThreadScheduledExecutor();
        this.timer.scheduleAtFixedRate(frameGrabber, 0, 50,
            TimeUnit.MILLISECONDS);

        // update the button content
        displayer.setCameraButtonText("Stop Camera");
      } else {
        // log the error
        System.err.println("Failed to open the camera connection...");
      }
    } else {
      displayer.setCameraButtonText("Start Camera");
      stopCamera();
    }
  }

  private void stopCamera() {
    // the camera is not active at this point
    this.cameraActive = false;
    // update again the button content
    // this.cameraButton.setText("Start Camera");

    // stop the timer
    try {
      this.timer.shutdown();
      this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      // log the exception
      System.err.println(
          "Exception in stopping the frame capture, trying to release the camera now... "
              + e);
    }

    // release the camera
    this.capture.release();

  }

  /**
   * Get a frame from the opened video stream (if any)
   *
   * @return the {@link Image} to show
   * @param imageCollector
   */
  private StartLight detectStartLight(ImageCollector imageCollector) {
    /* @FIXME - use ImageFetcher in calling part ...
    // init everything
    Image imageToShow = null;
    Mat frame = new Mat();

    // check if the capture is open
    if (this.capture.isOpened()) {
      try {
        // read the current frame
        this.capture.read(frame);

        // if the frame is not empty, process it
        if (!frame.empty()) {
          imageCollector.originalFrame(frame);
          startLineConfig.setHueStart(hueStart.getValue());
          startLineConfig.setHueStop(hueStop.getValue());
          startLineConfig.setSaturationStart(saturationStart.getValue());
          startLineConfig.setSaturationStop(saturationStop.getValue());
          startLineConfig.setValueStart(valueStart.getValue());
          startLineConfig.setValueStop(valueStop.getValue());

          StartLight detect = detector.withImageCollector(imageCollector)
              .detect(frame);

          if (lightOn) {
             numberOfLightOffEvents = 0;
          } else {
             numberOfLightOffEvents++;
          }
          // // show the current selected HSV range
          String valuesToPrint = String.format(
              "%4d: light %s h:%.1f-%.1f s:%.1f-%.1f v:%.1f-%.1f",
              ++frameIndex,
              lightOn?"on":"off",
              hueStart.getValue(),hueStop.getValue(),
              saturationStart.getValue(), saturationStop.getValue(),
              valueStart.getValue(),valueStop.getValue());
          displayer.showCurrentValues(valuesToPrint);
        }

      } catch (Exception e) {
        displayer.handle(e);
      }
    }
  */
    return null;
  }

  private void eventBusSendAfterMS(long ms, String command) {
    new java.util.Timer().schedule(new java.util.TimerTask() {
      @Override
      public void run() {
        System.out.println("Sending command '" + command + "'.");
        if (vertx != null) {
          vertx.eventBus().send("control", command);
        } else {
          System.out.println(
              "Couldn't send command '" + command + "', Vert.x not inited");
        }
      }
    }, ms);

  }

  /**
   * Given a binary image containing one or more closed surfaces, use it as a
   * mask to find and highlight the objects contours
   *
   * @param maskedImage
   *          the binary image to be used as a mask
   * @param frame
   *          the original {@link Mat} image to be used for drawing the objects
   *          contours
   * @return the {@link Mat} image with the objects contours framed
   */
  private Mat findAndDrawBalls(Mat maskedImage, Mat frame) {
    // init
    List<MatOfPoint> contours = new ArrayList<>();
    Mat hierarchy = new Mat();

    // find contours
    Imgproc.findContours(maskedImage, contours, hierarchy, Imgproc.RETR_CCOMP,
        Imgproc.CHAIN_APPROX_SIMPLE);

    boolean lightDetected = false;

    // if any contour exist...
    if (hierarchy.size().height > 0 && hierarchy.size().width > 0) {
      // for each contour, display it in blue
      for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {

        // Imgproc.drawMarker(frame, , new Scalar(250, 0, 0));
        // Imgproc.drawContours(frame, contours, idx, new Scalar(0, 0, 255));
        Imgproc.drawContours(frame, contours, idx, new Scalar(10, 250, 20), 1);
        lightDetected = true;

      }
    }

    lightOn = lightDetected;

    return frame;
  }
}
