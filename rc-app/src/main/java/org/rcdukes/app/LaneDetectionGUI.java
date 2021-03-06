package org.rcdukes.app;

import java.util.concurrent.TimeUnit;

import org.rcdukes.action.Navigator;
import org.rcdukes.camera.CameraMatrix;
import org.rcdukes.common.Config;
import org.rcdukes.detect.ImageObserver;
import org.rcdukes.detect.LaneDetector;
import org.rcdukes.detect.linedetection.HoughLinesLineDetector;
import org.rcdukes.geometry.LaneDetectionResult;
import org.rcdukes.video.ColorFilter;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageCollector;
import org.rcdukes.video.ImageSource;
import org.rcdukes.video.VideoRecorders;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

import org.rcdukes.detect.edgedectection.CannyEdgeDetector;

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
  private ColorPicker minColor;
  @FXML
  private ColorPicker maxColor;
  
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
  Observable<Image> simulatorImageObservable;
  private ImageObserver frameGrabber;
  // @TODO make configurable
  double fps = 15.0;

  public LaneDetectionGUI() {
    Config.configureLogging();
  }

  /**
   * ImageObserver for JavaFX
   * 
   * @author wf
   *
   */
  public class FrameGrabber extends ImageObserver {
    private CameraGUI cameraGUI;
    private VideoRecorders videoRecorders;

    /**
     * construct me for the given cameraGUI
     * 
     * @param displayer
     * @param cameraGUI
     * @param fps
     */
    public FrameGrabber(GUIDisplayer displayer, CameraGUI cameraGUI,
        double fps) {
      this.cameraGUI = cameraGUI;
      this.videoRecorders = new VideoRecorders(fps);
      displayer.setVideoRecorders(videoRecorders);
    }

    @Override
    public void onNext(Image originalImage) {
      try {
        super.onNext(originalImage);
        displayer.showFrameIndex(originalImage.getFrameIndex());
        applySliderValuesToConfig();
        cameraGUI.applySliderValues();
        ImageCollector collector = new ImageCollector();
        displayer.setImageCollector(collector);
        CameraMatrix cameraMatrix = CameraMatrix.DEFAULT;

        LaneDetector laneDetector = new LaneDetector(edgeDetector, lineDetector,
            cameraGUI.cameraConfig, cameraMatrix, collector);
        LaneDetectionResult ldr = laneDetector.detect(originalImage);
        ldr.checkError();
        Navigator navigator = LaneDetectionGUI.this.getAppVerticle()
            .getNavigator();
        if (navigator != null)
          navigator.navigateWithLaneDetectionResult(ldr);
        if (videoRecorders.isStarted())
          videoRecorders.recordFrame(collector,true);
        displayer.displayImageCollector(collector);
         } catch (Throwable t) {
        onError(t);
        // throw Exceptions.propagate(t);
      }
    }

    @Override
    public void onError(Throwable th) {
      super.onError(th);
      handleError(th);
    }
  }

  /**
   * start the camera from the given ImageSource
   * 
   * @param cameraGUI
   * @param imageSource
   * @throws Exception
   */
  public void startCamera(CameraGUI cameraGUI, ImageSource imageSource)
      throws Exception {
    configureGUI();
    if (this.frameGrabber == null) {
      frameGrabber = new FrameGrabber(displayer, cameraGUI, fps);
      displayer.setCameraButtonText("Stop Camera");
      Observable<Image> imageObservable = imageSource.getImageObservable();
      imageObservable.subscribeOn(Schedulers.newThread())
          .throttleFirst(Math.round(1000 / fps), TimeUnit.MILLISECONDS) // 10
                                                                        // Frames
                                                                        // Per
                                                                        // second
          // some 2.5 times slower
          // than original ...
          .subscribe(frameGrabber);
    } else {
      frameGrabber.stop();
      frameGrabber = null;
      displayer.setCameraButtonText("Start Camera");
    }
  }

  private void handleError(Throwable th) {
    th.printStackTrace();
    displayer.handle(th);
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
    Color min = minColor.getValue();
    Color max = maxColor.getValue();
    if (edgeDetector.getColorFilter()==null) {
      edgeDetector.setColorFilter(new ColorFilter());
    }
    ColorFilter colorFilter = edgeDetector.getColorFilter();
    colorFilter.setMinColorRGB(min.getRed(),min.getGreen(),min.getBlue());
    colorFilter.setMaxColorRGB(max.getRed(),max.getGreen(),max.getBlue());
    lineDetector.setProbabilistic(probabilistic.isSelected());
    lineDetector.setRho(lineDetectRho.getValue());
    lineDetector.setTheta(lineDetectTheta.getValue());
    lineDetector
        .setThreshold(new Double(lineDetectThreshold.getValue()).intValue());
    lineDetector.setMinLineLength(lineDetectMinLineLength.getValue());
    lineDetector.setMaxLineGap(lineDetectMaxLineGap.getValue());
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
