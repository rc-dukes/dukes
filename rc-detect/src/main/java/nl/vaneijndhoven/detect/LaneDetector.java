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

    private CannyEdgeDetector edgeDetector;
    private HoughLinesLineDetector lineDetector;
    private CameraMatrix matrix;
    ImageCollector collector;

    /**
     * create a LaneDetector
     * @param cannyConfig
     * @param lineDetectorConfig
     * @param matrix
     * @param collector
     */
    public LaneDetector(CannyEdgeDetector cannyDetector, HoughLinesLineDetector lineDetector, CameraMatrix matrix, ImageCollector collector) {
        this.setEdgeDetector(cannyDetector);
        this.setLineDetector(lineDetector);
        this.setMatrix(matrix);
        this.collector = collector;
    }

    public Map<String, Object> detect(Mat frame) {
        return performLaneDetection(frame);

    }

    public Map<String, Object> performLaneDetection(Mat originalImage) {
        ImageLaneDetection laneDetect = new ImageLaneDetection(this);
        return laneDetect.detectLane(originalImage, collector);
    }

    /**
     * @return the matrix
     */
    public CameraMatrix getMatrix() {
      return matrix;
    }

    /**
     * @param matrix the matrix to set
     */
    public void setMatrix(CameraMatrix matrix) {
      this.matrix = matrix;
    }

    /**
     * @return the edgeDetector
     */
    public CannyEdgeDetector getEdgeDetector() {
      return edgeDetector;
    }

    /**
     * @param edgeDetector the edgeDetector to set
     */
    public void setEdgeDetector(CannyEdgeDetector edgeDetector) {
      this.edgeDetector = edgeDetector;
    }

    /**
     * @return the lineDetector
     */
    public HoughLinesLineDetector getLineDetector() {
      return lineDetector;
    }

    /**
     * @param lineDetector the lineDetector to set
     */
    public void setLineDetector(HoughLinesLineDetector lineDetector) {
      this.lineDetector = lineDetector;
    }

}
