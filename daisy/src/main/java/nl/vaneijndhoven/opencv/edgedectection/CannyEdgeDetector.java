package nl.vaneijndhoven.opencv.edgedectection;

import nl.vaneijndhoven.opencv.tools.ImageCollector;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class CannyEdgeDetector implements EdgeDetector {

    private double threshold1 = 60;
    private double threshold2 = 150;
    private int apertureSize = 3;
    private boolean l2gradient = false;
    private Optional<ImageCollector> collector = empty();

    public CannyEdgeDetector() {}

    public CannyEdgeDetector(double threshold1, double threshold2, int apertureSize, boolean l2gradient) {
        this();
        this.threshold1 = threshold1;
        this.threshold2 = threshold2;
        this.apertureSize = apertureSize;
        this.l2gradient = l2gradient;
    }

    public CannyEdgeDetector(Config cfg) {
        this(cfg.getThreshold1(), cfg.getThreshold2(), cfg.getApertureSize(), cfg.getL2gradient());
    }

    public CannyEdgeDetector withImageCollector(ImageCollector collector) {
        this.collector = of(collector);
        return this;
    }

    @Override
    public Mat detect(Mat image) {
        Mat imgEdges = new Mat();
        Imgproc.Canny(image, imgEdges, threshold1, threshold2, apertureSize, l2gradient);
        collector.ifPresent(coll -> coll.edges(imgEdges));
        return imgEdges;
    }

    public static class Config {

        private double threshold1 = 60;
        private double threshold2 = 150;
        private int apertureSize = 3;
        private boolean l2gradient = false;

        public void setThreshold1(double threshold1) {
            this.threshold1 = threshold1;
        }

        public void setThreshold2(double threshold2) {
            this.threshold2 = threshold2;
        }

        public double getThreshold1() {
            return threshold1;
        }

        public double getThreshold2() {
            return threshold2;
        }

        public int getApertureSize() {
            return apertureSize;
        }

        public boolean getL2gradient() {
            return l2gradient;
        }
    }
}
