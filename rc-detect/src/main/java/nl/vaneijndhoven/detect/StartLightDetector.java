package nl.vaneijndhoven.daisy;

import nl.vaneijndhoven.objects.StartLight;
import nl.vaneijndhoven.opencv.tools.DebugTools;
import nl.vaneijndhoven.opencv.tools.ImageCollector;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;

public class StartLightDetector {

    private Config config = new Config();

    private StartLight startLight = StartLight.init();

    private DebugTools debug = new DebugTools.VoidTools();
    private Mat frame;
    private Optional<ImageCollector> collector = Optional.empty();

    public StartLightDetector() {}

    public StartLightDetector(int hueStart, int hueStop, int saturationStart, int saturationStop, int valueStart, int valueStop) {
        this();
        config.setHueStart(hueStart);
        config.setHueStop(hueStop);
        config.setSaturationStart(saturationStart);
        config.setSaturationStop(saturationStop);
        config.setValueStart(valueStart);
        config.setValueStop(valueStop);
    }

    public StartLightDetector(Config config) {
        this();
        this.config = config;
    }

    public StartLight detect(Mat image) {
        // init
//        Mat blurredImage = new Mat();
        Mat hsvImage = new Mat();
        Mat mask = new Mat();
        Mat morphOutput = new Mat();
// 768 * 576
        // int x, int y, int width, int height
        Rect rect = new Rect(250, 60, 40, 50);
        frame = new Mat(image, rect);
//        frame = new RegionOfInterest(0.33, 0.10, 0.05, 0.05).region(image);

        // convert the frame to HSV
        Imgproc.cvtColor(frame, hsvImage, Imgproc.COLOR_BGR2HSV);

        // get thresholding values from the UI
        // remember: H ranges 0-180, S and V range 0-255
        Scalar minValues = new Scalar(config.getHueStart(), config.getSaturationStart(),
                config.getValueStart());
        Scalar maxValues = new Scalar(config.getHueStop(), config.getSaturationStop(),
                config.getValueStop());

        // show the current selected HSV range
        String valuesToPrint = "Hue range: " + minValues.val[0] + "-" + maxValues.val[0]
                + "\tSaturation range: " + minValues.val[1] + "-" + maxValues.val[1] + "\tValue range: "
                + minValues.val[2] + "-" + maxValues.val[2];
//        this.onFXThread(this.hsvValuesProp, valuesToPrint);

        // threshold HSV image to select tennis balls
        Core.inRange(hsvImage, minValues, maxValues, mask);
        // show the partial output
        collector.ifPresent(coll -> coll.mask(mask));

        // morphological operators
        // dilate with large element, erode with small ones
        Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
        Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

        Imgproc.erode(mask, morphOutput, erodeElement);
        Imgproc.erode(mask, morphOutput, erodeElement);

        Imgproc.dilate(mask, morphOutput, dilateElement);
        Imgproc.dilate(mask, morphOutput, dilateElement);

        // show the partial output
        collector.ifPresent(coll -> coll.morph(morphOutput));

        // find the tennis ball(s) contours and show them
        return this.detect(morphOutput, frame);
    }

    /**
     * Given a binary image containing one or more closed surfaces, use it as a
     * mask to find and highlight the objects contours
     *
     * @param maskedImage the binary image to be used as a mask
     * @param frame       the original {@link Mat} image to be used for drawing the
     *                    objects contours
     * @return the {@link Mat} image with the objects contours framed
     */
    private StartLight detect(Mat maskedImage, Mat frame) {
        // init
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        // find contours
        Imgproc.findContours(maskedImage, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

        StartLight startLight = this.startLight;
        // if any contour exist...
        if (hierarchy.size().height > 0 && hierarchy.size().width > 0) {
            // for each contour, display it in blue
            for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {

//                Imgproc.drawMarker(frame,   ,  new Scalar(250, 0, 0));
                // Imgproc.drawContours(frame, contours, idx, new Scalar(0, 0, 255));
                Imgproc.drawContours(frame, contours, idx, new Scalar(10, 250, 20), 10);
            }

            startLight = startLight.on();
        } else {
            startLight = startLight.off();
        }

        collector.ifPresent(coll -> coll.startLight(frame));
        return startLight;
    }

    public StartLightDetector withDebug(DebugTools debug) {
        this.debug = debug;
        return this;
    }

    public Mat getFrame() {
        return frame;
    }

    public StartLightDetector withImageCollector(ImageCollector collector) {
        this.collector = of(collector);
        return this;
    }

    public static class Config {
        private double hueStart = 0.0d;
        private double hueStop = 28.3d;
        private double saturationStart = 71.9d;
        private double saturationStop = 98.7d;
        private double valueStart = 213.9d;
        private double valueStop = 240.6d;

        public double getHueStart() {
            return hueStart;
        }

        public void setHueStart(double hueStart) {
            this.hueStart = hueStart;
        }

        public double getHueStop() {
            return hueStop;
        }

        public void setHueStop(double hueStop) {
            this.hueStop = hueStop;
        }

        public double getSaturationStart() {
            return saturationStart;
        }

        public void setSaturationStart(double saturationStart) {
            this.saturationStart = saturationStart;
        }

        public double getSaturationStop() {
            return saturationStop;
        }

        public void setSaturationStop(double saturationStop) {
            this.saturationStop = saturationStop;
        }

        public double getValueStart() {
            return valueStart;
        }

        public void setValueStart(double valueStart) {
            this.valueStart = valueStart;
        }

        public double getValueStop() {
            return valueStop;
        }

        public void setValueStop(double valueStop) {
            this.valueStop = valueStop;
        }
    }
}
