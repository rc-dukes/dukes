package nl.vaneijndhoven.detect;

import java.util.Map;

import org.opencv.core.Mat;

import nl.vaneijndhoven.dukes.camera.matrix.CameraMatrix;
import nl.vaneijndhoven.opencv.edgedectection.CannyEdgeDetector;
import nl.vaneijndhoven.opencv.lanedetection.ImageLaneDetection;
import nl.vaneijndhoven.opencv.linedetection.HoughLinesLineDetector;
import nl.vaneijndhoven.opencv.tools.ImageCollector;

/**
 * detector for lanes
 *
 */
public class LaneDetector {

    public static double DEFAULT_LANE_BOUNDARY_ANGLE = 45;
    public static double DEFAULT_LANE_BOUNDARY_TOLERANCE = 30;

    public static final double DEFAULT_CANNY_THRESHOLD_1 = 131d;
    public static final double DEFAULT_CANNY_THRESHOLD_2 = 397d;
    public static final int DEFAULT_LINE_DETECT_RHO = 1;
    public static final double DEFAULT_LINE_DETECT_THETA = Math.PI / 180;
    public static final double DEFAULT_LINE_DETECT_THRESHOLD = 42d;
    public static final double DEFAULT_LINE_DETECT_MIN_LINE_LENGTH = 0d;
    public static final double DEFAULT_LINE_DETECT_MAX_LINE_GAP = 98d;

    private CannyEdgeDetector.Config cannyConfig;
    private HoughLinesLineDetector.Config lineDetectorConfig;
    private CameraMatrix matrix;
    private ImageCollector collector;


    public LaneDetector(CannyEdgeDetector.Config cannyConfig, HoughLinesLineDetector.Config lineDetectorConfig, CameraMatrix matrix, ImageCollector collector) {
        this.cannyConfig = cannyConfig;
        this.lineDetectorConfig = lineDetectorConfig;
        this.matrix = matrix;
        this.collector = collector;
    }

    public Map<String, Object> detect(Mat frame) {
        return performLaneDetection(frame);

    }

    public Map<String, Object> performLaneDetection(Mat originalImage) {
        ImageLaneDetection laneDetect = new ImageLaneDetection(cannyConfig, lineDetectorConfig, matrix);
        return laneDetect.detectLane(originalImage, collector);
    }

    public void setCannyConfig(CannyEdgeDetector.Config cannyConfig) {
        this.cannyConfig = cannyConfig;
    }

    public void setLineDetectorConfig(HoughLinesLineDetector.Config lineDetectorConfig) {
        this.lineDetectorConfig = lineDetectorConfig;
    }

    public void setCollector(ImageCollector collector) {
        this.collector = collector;
    }
}
