package org.rcdukes.app;

import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.rcdukes.camera.CameraMatrix;
import org.rcdukes.detect.CameraConfig;
import org.rcdukes.detect.ImageFetcher;
import org.rcdukes.detect.ImageSubscriber;
import org.rcdukes.detect.LaneDetector;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageCollector;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import org.rcdukes.common.Config;
import nl.vaneijndhoven.opencv.edgedectection.CannyEdgeDetector;
import nl.vaneijndhoven.opencv.linedetection.HoughLinesLineDetector;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Graphical user interface for lane detection
 * @author wf
 *
 */
public class LaneDetectionGUI extends BaseGUI {
  @FXML
  private CheckBox probabilistic;
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

  private CannyEdgeDetector edgeDetector = new CannyEdgeDetector();
  private HoughLinesLineDetector lineDetector = new HoughLinesLineDetector();

  private boolean configured = false;
  private Subscription imageSubscriber=null;

  public LaneDetectionGUI() {
    Config.configureLogging();
    initVertx();
  }

  public void startCamera() throws Exception {
    configureGUI();

    if (this.imageSubscriber==null) {
        ImageFetcher imageFetcher = new ImageFetcher(
            displayer.getLaneVideoProperty().getValue());
        ImageSubscriber frameGrabber = new ImageSubscriber() {
          @Override
          public void onNext(Image originalImage) {
            super.onNext(originalImage);
            displayCurrentSliderValues();
            applySliderValuesToConfig();
            displayer.displayOriginal(originalImage.getFrame());
            ImageCollector collector = new ImageCollector();
            CameraMatrix cameraMatrix = CameraMatrix.DEFAULT;
            CameraConfig cameraConfig=new CameraConfig();
            LaneDetector laneDetector=new LaneDetector(edgeDetector,lineDetector,cameraConfig,cameraMatrix,collector);
            laneDetector.detect(originalImage);
            displayer.display1(collector.edges());
            displayer.display2(collector.lines());
          }
        };
        displayer.setCameraButtonText("Stop Camera");
        imageSubscriber = imageFetcher.toObservable()
            .subscribeOn(Schedulers.newThread())
            .throttleFirst(100, TimeUnit.MILLISECONDS) // 10 Frames Per second some 2.5 times slower than original ...
            .doOnError(th->displayer.handle(th))
            .subscribe(frameGrabber);
    } else {
      imageSubscriber.unsubscribe();
      imageSubscriber=null;
      displayer.setCameraButtonText("Start Camera");
    }
  }

  private void configureGUI() {
    if (!configured) {
      configureSliderDefaults();
      configured = true;
    }
  }

  private void applySliderValuesToConfig() {
    edgeDetector.setThreshold1(cannyThreshold1.getValue());
    edgeDetector.setThreshold2(cannyThreshold2.getValue());
    lineDetector.setProbabilistic(probabilistic.isSelected());
    lineDetector.setRho(lineDetectRho.getValue());
    lineDetector.setTheta(lineDetectTheta.getValue());
    lineDetector
        .setThreshold(new Double(lineDetectThreshold.getValue()).intValue());
    lineDetector.setMinLineLength(lineDetectMinLineLength.getValue());
    lineDetector.setMaxLineGap(lineDetectMaxLineGap.getValue());
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
    CannyEdgeDetector ed = new CannyEdgeDetector();
    this.cannyThreshold1.setValue(ed.getThreshold1());
    this.cannyThreshold2.setValue(ed.getThreshold2());
    HoughLinesLineDetector ld = new HoughLinesLineDetector();
    this.probabilistic.setSelected(ld.isProbabilistic());
    this.lineDetectRho.setValue(ld.getRho());
    this.lineDetectTheta.setValue(ld.getTheta());
    this.lineDetectThreshold.setValue(ld.getThreshold());
    this.lineDetectMinLineLength.setValue(ld.getMinLineLength());
    this.lineDetectMaxLineGap.setValue(ld.getMaxLineGap());
  }

  private void initVertx() {
    VertxOptions options = new VertxOptions().setClustered(true)
        .setClusterManager(Config.createHazelcastConfig());

    Vertx.clusteredVertx(options, resultHandler -> {
      Vertx vertx = resultHandler.result();
      this.vertx = vertx;

      Runtime.getRuntime().addShutdownHook(new Thread() {
        public void run() {
          vertx.close();
        }
      });
    });
  }
}
