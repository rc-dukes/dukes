//package nl.vaneijndhoven.dukes.unclejesse;
//
//import io.vertx.core.Vertx;
//import io.vertx.core.VertxOptions;
//import javafx.application.Platform;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.Slider;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import nl.vaneijndhoven.objects.StartLight;
//import nl.vaneijndhoven.opencv.startlightdetection.DefaultStartLightDetector;
//import nl.vaneijndhoven.opencv.startlightdetection.StartLightDetector;
//import nl.vaneijndhoven.opencv.tools.ImageCollector;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.videoio.VideoCapture;
//
//import java.io.ByteArrayInputStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//import static nl.vaneijndhoven.opencv.video.LoggingUtils.createHazelcastConfig;
//
//public class StartLightDetectionController {
//
//    private Vertx vertx;
//    private DefaultStartLightDetector.Config startLineConfig = new DefaultStartLightDetector.Config();
//    private StartLightDetector detector = new DefaultStartLightDetector();
//
//    public StartLightDetectionController() {
//        Config.configureLogging();
//        VertxOptions options = new VertxOptions()
//                .setClustered(true)
//                .setClusterManager(createHazelcastConfig());
//
//        Vertx.clusteredVertx(options, resultHandler -> {
//            Vertx vertx = resultHandler.result();
//            this.vertx = vertx;
//        });
//
//    }
//
//    private boolean lightOn = false;
//
//    String lastCommandSent = "";
//
//    private int numberOfLightOffEvents = 0;
//
//    // FXML camera button
//    @FXML
//    private Button cameraButton;
//    // the FXML area for showing the current frame
//    @FXML
//    private ImageView originalFrame;
//    // the FXML area for showing the mask
//    @FXML
//    private ImageView maskImage;
//    // the FXML area for showing the output of the morphological operations
//    @FXML
//    private ImageView morphImage;
//    // FXML slider for setting HSV ranges
//    @FXML
//    private Slider hueStart;
//    @FXML
//    private Slider hueStop;
//    @FXML
//    private Slider saturationStart;
//    @FXML
//    private Slider saturationStop;
//    @FXML
//    private Slider valueStart;
//    @FXML
//    private Slider valueStop;
//    // FXML label to show the current values set with the sliders
//    @FXML
//    private Label hsvCurrentValues;
//
//    // a timer for acquiring the video stream
//    private ScheduledExecutorService timer;
//    // the OpenCV object that performs the video capture
//    private VideoCapture capture = new VideoCapture();
//    // a flag to change the button behavior
//    private boolean cameraActive;
//
//    // property for object binding
//    private ObjectProperty<String> hsvValuesProp;
//
//    @FXML
//    private void startCamera() {
//        // bind a text property with the string containing the current range of
//        // HSV values for object detection
//        hsvValuesProp = new SimpleObjectProperty<>();
//        this.hsvCurrentValues.textProperty().bind(hsvValuesProp);
//
//        // set a fixed width for all the image to show and preserve image ratio
//        this.imageViewProperties(this.originalFrame, 400);
//        this.imageViewProperties(this.maskImage, 200);
//        this.imageViewProperties(this.morphImage, 200);
//
//
//        this.hueStart.adjustValue(0.0d);
//        this.hueStop.adjustValue(28.3d);
//
//        this.saturationStart.adjustValue(71.9d);
//        this.saturationStop.adjustValue(98.7);
//
//        this.valueStart.adjustValue(213.9d);
//        this.valueStop.adjustValue(240.6d);
//
//        if (!this.cameraActive) {
//            // start the video capture
////            this.capture.open(0);
//            this.capture.open("file://Users/jpoint/Repositories/opencv-playground/src/main/resources/videos/startlamp2.m4v");
////            this.capture.open("http://10.9.8.7/html/cam_pic_new.php?time=1472218786342&pDelay=66666");
//
//            // is the video stream available?
//            if (this.capture.isOpened()) {
//                this.cameraActive = true;
//
//                // grab a frame every 33 ms (30 frames/sec)
//                Runnable frameGrabber = () -> {
//                    ImageCollector imageCollector = new ImageCollector();
////                    detector.withImageCollector(imageCollector);
//                    StartLight startLight = detectStartLight(imageCollector);
//                    originalFrame.setImage(new Image(new ByteArrayInputStream(imageCollector.originalFrame())));
////                    System.out.println("light on: " + lightOn);
//
////                    if (lightOn) {
////                        numberOfLightOffEvents = 0;
////                    } else {
////                        numberOfLightOffEvents++;
////                    }
//
//                    if (this.vertx != null) {
//
////                        int minimumNumberOfLightOffEvents = 2;
//                        if (startLight.started() && !lastCommandSent.equals("on")) {
//                            System.out.println("Enough light off events received, starting");
//                            // power on
//                            lastCommandSent = "on";
//                            eventBusSendAfterMS(10, "setspeed:3"); // in percentage
////                            eventBusSendAfterMS(100, "speed:up");
//                           // eventBusSendAfterMS(30, "speed:up");
//
//
//                            // vertx.eventBus().send("control", "speed:up");
//                            //vertx.eventBus().send("control", "speed:up");
//
//
//                            Executors.newSingleThreadScheduledExecutor().schedule(() -> stopCamera(), 500, TimeUnit.MILLISECONDS);
//
//
//                            //vertx.eventBus().send("control", "speed:up");
////                            vertx.eventBus().send("control", "speed:up");
////                            vertx.eventBus().send("control", "speed:up");
////                            Platform.runLater(() -> this.cameraButton.setText("Stop Camera"));
//
//                            // this.cameraButton.setText("Start Camera");
////                            stopCamera();
////                            try {
////                                Thread.sleep(500);
////                            } catch (InterruptedException e) {
////                                e.printStackTrace();
////                            }
////                            stopCamera();
//
//
//
//
//                        }
//
//                        if (!startLight.started() && !lastCommandSent.equals("off")) {
//                            // power off
//                            System.out.println("light is on, stopping");
//                            lastCommandSent = "off";
//                            // eventBusSendAfterMS(10, "speed:stop");
//
//                        }
//
//                    }
//
//
//                };
//
//                this.timer = Executors.newSingleThreadScheduledExecutor();
//                this.timer.scheduleAtFixedRate(frameGrabber, 0, 50, TimeUnit.MILLISECONDS);
//
//                // update the button content
//                this.cameraButton.setText("Stop Camera");
//            } else {
//                // log the error
//                System.err.println("Failed to open the camera connection...");
//            }
//        } else {
//            this.cameraButton.setText("Start Camera");
//            stopCamera();
//        }
//    }
//
//    private void stopCamera() {
//        // the camera is not active at this point
//        this.cameraActive = false;
//        // update again the button content
//        // this.cameraButton.setText("Start Camera");
//
//        // stop the timer
//        try {
//            this.timer.shutdown();
//            this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            // log the exception
//            System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
//        }
//
//        // release the camera
//        this.capture.release();
//
//    }
//    /**
//     * Get a frame from the opened video stream (if any)
//     *
//     * @return the {@link Image} to show
//     * @param imageCollector
//     */
//    private StartLight detectStartLight(ImageCollector imageCollector) {
//        // init everything
//        Image imageToShow = null;
//        Mat frame = new Mat();
//
//        // check if the capture is open
//        if (this.capture.isOpened()) {
//            try {
//                // read the current frame
//                this.capture.read(frame);
//
//                // if the frame is not empty, process it
//                if (!frame.empty()) {
//                    imageCollector.originalFrame(frame);
//                    startLineConfig.setHueStart(hueStart.getValue());
//                    startLineConfig.setHueStop(hueStop.getValue());
//                    startLineConfig.setSaturationStart(saturationStart.getValue());
//                    startLineConfig.setSaturationStop(saturationStop.getValue());
//                    startLineConfig.setValueStart(valueStart.getValue());
//                    startLineConfig.setValueStop(valueStop.getValue());
//
//                    StartLight detect = detector.withImageCollector(imageCollector).detect(frame);
//
////                    // show the current selected HSV range
//                    String valuesToPrint = "Hue range: " + hueStart.getValue() + "-" + hueStop.getValue()
//                            + "\tSaturation range: " + saturationStart.getValue() + "-" + saturationStop.getValue() + "\tValue range: "
//                            + valueStart.getValue() + "-" + valueStop.getValue();
//                    this.onFXThread(this.hsvValuesProp, valuesToPrint);
//                    this.onFXThread(this.maskImage.imageProperty(), new Image(new ByteArrayInputStream(imageCollector.mask())));
//                    this.onFXThread(this.morphImage.imageProperty(), new Image(new ByteArrayInputStream(imageCollector.morph())));
//                    this.onFXThread(this.originalFrame.imageProperty(), new Image(new ByteArrayInputStream(imageCollector.startLight())));
//                }
//
//            } catch (Exception e) {
//                // log the (full) error
//                System.err.print("ERROR");
//                e.printStackTrace();
//            }
//        }
//
//        return null;
//    }
//
//
//
//    private void eventBusSendAfterMS(long ms, String command) {
//        new java.util.Timer().schedule(
//                new java.util.TimerTask() {
//                    @Override
//                    public void run() {
//                        System.out.println("Sending command '" + command + "'.");
//                        if (vertx != null) {
//                            vertx.eventBus().send("control", command);
//                        } else {
//                            System.out.println("Couldn't send command '" + command + "', Vert.x not inited");
//                        }
//                    }
//                },
//                ms
//        );
//
//    }
//
//    /**
//     * Given a binary image containing one or more closed surfaces, use it as a
//     * mask to find and highlight the objects contours
//     *
//     * @param maskedImage the binary image to be used as a mask
//     * @param frame       the original {@link Mat} image to be used for drawing the
//     *                    objects contours
//     * @return the {@link Mat} image with the objects contours framed
//     */
//    private Mat findAndDrawBalls(Mat maskedImage, Mat frame) {
//        // init
//        List<MatOfPoint> contours = new ArrayList<>();
//        Mat hierarchy = new Mat();
//
//        // find contours
//        Imgproc.findContours(maskedImage, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        boolean lightDetected = false;
//
//        // if any contour exist...
//        if (hierarchy.size().height > 0 && hierarchy.size().width > 0) {
//            // for each contour, display it in blue
//            for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {
//
////                Imgproc.drawMarker(frame,   ,  new Scalar(250, 0, 0));
//                // Imgproc.drawContours(frame, contours, idx, new Scalar(0, 0, 255));
//                Imgproc.drawContours(frame, contours, idx, new Scalar(10, 250, 20), 1);
//                lightDetected = true;
//
//            }
//        }
//
//
//        lightOn = lightDetected;
//
//        return frame;
//    }
//
//    /**
//     * Set typical {@link ImageView} properties: a fixed width and the
//     * information to preserve the original image ration
//     *
//     * @param image     the {@link ImageView} to use
//     * @param dimension the width of the image to set
//     */
//    private void imageViewProperties(ImageView image, int dimension) {
//        // set a fixed width for the given ImageView
//        image.setFitWidth(dimension);
//        // preserve the image ratio
//        image.setPreserveRatio(true);
//    }
//
//    /**
//     * Convert a {@link Mat} object (OpenCV) in the corresponding {@link Image}
//     * for JavaFX
//     *
//     * @param frame the {@link Mat} representing the current frame
//     * @return the {@link Image} to show
//     */
//    private Image mat2Image(Mat frame) {
//        // create a temporary buffer
//        MatOfByte buffer = new MatOfByte();
//        // encode the frame in the buffer, according to the PNG format
//        Imgcodecs.imencode(".png", frame, buffer);
//        // build and return an Image created from the image encoded in the
//        // buffer
//        return new Image(new ByteArrayInputStream(buffer.toArray()));
//    }
//
//    /**
//     * Generic method for putting element running on a non-JavaFX thread on the
//     * JavaFX thread, to properly update the UI
//     *
//     * @param property a {@link ObjectProperty}
//     * @param value    the value to set for the given {@link ObjectProperty}
//     */
//    private <T> void onFXThread(final ObjectProperty<T> property, final T value) {
//        Platform.runLater(() -> property.set(value));
//
//
//    }
//}
