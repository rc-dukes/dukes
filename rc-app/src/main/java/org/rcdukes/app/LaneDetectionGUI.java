package org.rcdukes.app;

import java.util.concurrent.TimeUnit;

import org.rcdukes.camera.CameraMatrix;
import org.rcdukes.common.Config;
import org.rcdukes.detect.ImageFetcher;
import org.rcdukes.detect.ImageSubscriber;
import org.rcdukes.detect.LaneDetector;
import org.rcdukes.detect.linedetection.HoughLinesLineDetector;
import org.rcdukes.geometry.LaneDetectionResult;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageCollector;
import org.rcdukes.video.ImageSource;
import org.rcdukes.video.ImageCollector.ImageType;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import nl.vaneijndhoven.opencv.edgedectection.CannyEdgeDetector;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import org.reactivestreams.Subscription;
import io.reactivex.schedulers.Schedulers;

/**
 * Graphical user interface for lane detection
 * 
 * @author wf
 *
 */
public class LaneDetectionGUI extends BaseGUI {
  @FXML
  private CheckBox probabilistic;
  @FXML
  private LabeledValueSlider cannyThreshold1;
  @FXML
  private LabeledValueSlider cannyThreshold2;
  @FXML
  private LabeledValueSlider lineDetectRho;
  @FXML
  private LabeledValueSlider lineDetectTheta;
  @FXML
  private LabeledValueSlider lineDetectThreshold;
  @FXML
  private LabeledValueSlider lineDetectMinLineLength;
  @FXML
  private LabeledValueSlider lineDetectMaxLineGap;

  private CannyEdgeDetector edgeDetector = new CannyEdgeDetector();
  private HoughLinesLineDetector lineDetector = new HoughLinesLineDetector();

  private boolean configured = false;
  private Disposable imageSubscriber = null;
  Observable<Image> simulatorImageObservable;

  public LaneDetectionGUI() {
    Config.configureLogging();
  }

  public void startCamera(CameraGUI cameraGUI, ImageSource imageSource) throws Exception {
    configureGUI();

    if (this.imageSubscriber == null) {
      ImageSubscriber frameGrabber = new ImageSubscriber() {
        @Override
        public void onNext(Image originalImage) {
          super.onNext(originalImage);
          if (debug)
            displayCurrentSliderValues();
          applySliderValuesToConfig();
          cameraGUI.applySliderValues();
          displayer.displayOriginal(originalImage.getFrame());
          ImageCollector collector = new ImageCollector();
          CameraMatrix cameraMatrix = CameraMatrix.DEFAULT;
          LaneDetector laneDetector = new LaneDetector(edgeDetector,
              lineDetector, cameraGUI.cameraConfig, cameraMatrix, collector);
          LaneDetectionResult ldr = laneDetector.detect(originalImage);
          displayer.display1(collector.getImage(ImageType.edges, true));
          displayer.display2(collector.getImage(ImageType.lines, true));
        }
      };
      displayer.setCameraButtonText("Stop Camera");
      Observable<Image> imageObservable=imageSource.getImageObservable();
      imageSubscriber = imageObservable.subscribeOn(Schedulers.newThread())
          .throttleFirst(100, TimeUnit.MILLISECONDS) // 10 Frames Per second
                                                     // some 2.5 times slower
                                                     // than original ...
          .doOnError(th -> displayer.handle(th)).subscribe(frameGrabber);
    } else {
      imageSubscriber.dispose();
      imageSubscriber = null;
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

}
