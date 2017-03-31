package nl.vaneijndhoven.opencv.video;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nl.vaneijndhoven.opencv.edgedectection.CannyEdgeDetector;
import nl.vaneijndhoven.opencv.linedetection.ProbabilisticHoughLinesLineDetector;
import nl.vaneijndhoven.opencv.tools.ImageCollector;
import nl.vaneijndhoven.opencv.tools.MemoryManagement;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.util.concurrent.*;

import static nl.vaneijndhoven.opencv.tools.MemoryManagement.closable;
import static nl.vaneijndhoven.opencv.video.LaneDetectionController.*;
import static nl.vaneijndhoven.opencv.video.LoggingUtils.createHazelcastConfig;

public class LaneDetectionGUI {
    @FXML private Button cameraButton;
    @FXML private ImageView originalFrame;
    @FXML private ImageView processedImage1;
    @FXML private ImageView processedImage2;
    @FXML private Slider cannyThreshold1;
    @FXML private Slider cannyThreshold2;
    @FXML private Slider lineDetectRho;
    @FXML private Slider lineDetectTheta;
    @FXML private Slider lineDetectThreshold;
    @FXML private Slider lineDetectMinLineLength;
    @FXML private Slider lineDetectMaxLineGap;
    @FXML private Label sliderCurrentValues;

    private Vertx vertx;
    private ScheduledExecutorService timer;
    private VideoCapture capture = new VideoCapture();
    private boolean cameraActive;
    private ObjectProperty<String> sliderValuesProp;

    private CannyEdgeDetector.Config cannyConfig = new CannyEdgeDetector.Config();
    private ProbabilisticHoughLinesLineDetector.Config houghLinesConfig = new ProbabilisticHoughLinesLineDetector.Config();
    private LaneDetectionController controller = null;

    private boolean configured = false;

    public LaneDetectionGUI() {
        LoggingUtils.configureLogging();
        initVertx();
    }

    @FXML
    public void startCamera() {
        configureGUI();

        // on start, reset default values in controller
        controller = new LaneDetectionController(vertx);

        if (!this.cameraActive) {
            // load test file
//             this.capture.open("file://Users/jpoint/Repositories/opencv-playground/src/main/resources/videos/stopzone2.m4v");
             this.capture.open("file://Users/jpoint/Repositories/opencv-playground/src/main/resources/videos/full_run.mp4");

            // webcam stream
            // this.capture.open(0);

            // stream from pi
//            this.capture.open("http://10.9.8.7/html/cam_pic_new.php?time=1472218786342&pDelay=66666");

            if (this.capture.isOpened()) {
                this.cameraActive = true;

                Runnable frameGrabber = () -> {
                    try {
                        displayCurrentSliderValues();
                        applySliderValuesToConfig();


                        try (MemoryManagement.ClosableMat<Mat> originalImage = closable(grabFrame())) {
                            displayImage(originalFrame, originalImage.get());

                            ImageCollector collector = new ImageCollector();
                            controller.performLaneDetection(originalImage.get(), cannyConfig, houghLinesConfig, collector);

                            displayImage(processedImage1, collector.edges());
                            displayImage(processedImage2, collector.lines());
                        }
                    } catch (Exception e) {
                        System.out.println("Exception detected: ");
                        e.printStackTrace();
                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                // this.timer = Executors.newScheduledThreadPool(100);
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 50, TimeUnit.MILLISECONDS);

                this.cameraButton.setText("Stop Camera");
            } else {
                System.err.println("Failed to open the camera connection...");
            }
        } else {
            this.cameraActive = false;
            this.cameraButton.setText("Start Camera");

            try {
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }

            this.capture.release();
        }
    }

    private void configureGUI() {
        if (!configured) {
            sliderValuesProp = new SimpleObjectProperty<>();
            sliderCurrentValues.textProperty().bind(sliderValuesProp);

            configureImageDisplaySize();
            configureSliderDefaults();
            configured = true;
        }
    }

    private void configureImageDisplaySize() {
        this.imageViewProperties(this.originalFrame, 400);
        this.imageViewProperties(this.processedImage1, 400);
        this.imageViewProperties(this.processedImage2, 400);
    }

    private void applySliderValuesToConfig() {
        cannyConfig.setThreshold1(cannyThreshold1.getValue());
        cannyConfig.setThreshold2(cannyThreshold2.getValue());
        houghLinesConfig.setRho(lineDetectRho.getValue());
        houghLinesConfig.setTheta(lineDetectTheta.getValue());
        houghLinesConfig.setThreshold(new Double(lineDetectThreshold.getValue()).intValue());
        houghLinesConfig.setMinLineLength(lineDetectMinLineLength.getValue());
        houghLinesConfig.setMaxLineGap(lineDetectMaxLineGap.getValue());
    }

    private void displayCurrentSliderValues() {
        // show the current selected HSV range
        String valuesToPrint = "canny1: " + cannyThreshold1.getValue() +
                ", canny2: " + cannyThreshold2.getValue() +
                ", rho: " + lineDetectRho.getValue() +
                ", theta: " + lineDetectTheta.getValue() +
                ", threshold: " + lineDetectThreshold.getValue() +
                ", minLength: " + lineDetectMinLineLength.getValue() +
                ", maxGap: " + lineDetectMaxLineGap.getValue();

        this.onFXThread(this.sliderValuesProp, valuesToPrint);
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

    private void imageViewProperties(ImageView image, int dimension) {
        image.setFitWidth(dimension);
        image.setPreserveRatio(true);
    }

    private void displayImage(ImageView fxImage, Mat openCvImage) {
        this.onFXThread(fxImage.imageProperty(), ImageUtils.mat2Image(openCvImage));
    }

    private void displayImage(ImageView fxImage, byte[] imageData) {
        this.onFXThread(fxImage.imageProperty(), new Image(new ByteArrayInputStream(imageData)));
    }

    private <T> void onFXThread(final ObjectProperty<T> property, final T value) {
        Platform.runLater(() -> property.set(value));
    }

    private void initVertx() {
        VertxOptions options = new VertxOptions()
                .setClustered(true)
                .setClusterManager(createHazelcastConfig());

        Vertx.clusteredVertx(options, resultHandler -> {
            Vertx vertx = resultHandler.result();
            this.vertx = vertx;
            controller = new LaneDetectionController(vertx);


            Runtime.getRuntime().addShutdownHook(new Thread(){
                public void run() {
                    vertx.close();
                }
            });


        });
    }



}
