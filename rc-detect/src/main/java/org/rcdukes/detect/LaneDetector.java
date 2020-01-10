package org.rcdukes.detect;

import java.util.Map;

import org.opencv.core.Mat;
import org.rcdukes.detectors.EdgeDetector;
import org.rcdukes.detectors.LineDetector;
import org.rcdukes.video.ImageCollector;

import nl.vaneijndhoven.dukes.camera.matrix.CameraMatrix;
import nl.vaneijndhoven.opencv.edgedectection.CannyEdgeDetector;
import nl.vaneijndhoven.opencv.lanedetection.ImageLaneDetection;
import nl.vaneijndhoven.opencv.linedetection.HoughLinesLineDetector;

/**
 * detector for lanes
 *
 */
public class LaneDetector {

    private EdgeDetector edgeDetector;
    private LineDetector lineDetector;
    private CameraMatrix matrix;
    private ImageCollector collector;

    public static LaneDetector getDefault() {
      ImageCollector collector = new ImageCollector();
      CameraMatrix matrix=CameraMatrix.DEFAULT;
      CannyEdgeDetector edgeDetector=new CannyEdgeDetector();
      HoughLinesLineDetector lineDetector=new HoughLinesLineDetector();
      LaneDetector laneDetector = new LaneDetector(edgeDetector.withImageCollector(collector),lineDetector.withImageCollector(collector),matrix,collector);
      return laneDetector;
    }
    /**
     * create a Lane Detector
     * @param edgeDetector
     * @param lineDetector
     * @param matrix
     * @param collector
     */
    public LaneDetector(EdgeDetector edgeDetector, LineDetector lineDetector, CameraMatrix matrix, ImageCollector collector) {
        this.setEdgeDetector(edgeDetector);
        this.setLineDetector(lineDetector);
        this.setMatrix(matrix);
        this.setCollector(collector);
    }

    public Map<String, Object> detect(Mat frame) {
        return performLaneDetection(frame);
    }

    public Map<String, Object> performLaneDetection(Mat originalImage) {
        ImageLaneDetection laneDetect = new ImageLaneDetection(this);
        return laneDetect.detectLane(originalImage, getCollector());
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
    public EdgeDetector getEdgeDetector() {
      return edgeDetector;
    }

    /**
     * @param edgeDetector the edgeDetector to set
     */
    public void setEdgeDetector(EdgeDetector edgeDetector) {
      this.edgeDetector = edgeDetector;
    }

    /**
     * @return the lineDetector
     */
    public LineDetector getLineDetector() {
      return lineDetector;
    }

    /**
     * @param lineDetector the lineDetector to set
     */
    public void setLineDetector(LineDetector lineDetector) {
      this.lineDetector = lineDetector;
    }
    /**
     * @return the collector
     */
    public ImageCollector getCollector() {
      return collector;
    }
    /**
     * @param collector the collector to set
     */
    public void setCollector(ImageCollector collector) {
      this.collector = collector;
    }

}
