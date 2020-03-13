package org.rcdukes.detect;

import org.rcdukes.camera.CameraMatrix;
import org.rcdukes.detect.edgedectection.CannyEdgeDetector;
import org.rcdukes.detect.lanedetection.ImageLaneDetection;
import org.rcdukes.detect.linedetection.HoughLinesLineDetector;
import org.rcdukes.detectors.EdgeDetector;
import org.rcdukes.detectors.LineDetector;
import org.rcdukes.geometry.LaneDetectionResult;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageCollector;

/**
 * detector for lanes
 *
 */
public class LaneDetector {

  private EdgeDetector edgeDetector;
  private LineDetector lineDetector;
  private CameraMatrix matrix;
  private CameraConfig cameraConfig;

  private ImageCollector collector;

  /**
   * get a default lanedetector
   * @return - the lane detector
   */
  public static LaneDetector getDefault() {
    ImageCollector collector = new ImageCollector();
    CameraMatrix matrix = CameraMatrix.DEFAULT;
    EdgeDetector edgeDetector = new CannyEdgeDetector();
    CameraConfig cameraConfig = new CameraConfig();
    LineDetector lineDetector = new HoughLinesLineDetector();
    LaneDetector laneDetector = new LaneDetector(
        edgeDetector,
        lineDetector, cameraConfig, matrix,
        collector);
    return laneDetector;
  }

  /**
   * create a Lane Detector
   * 
   * @param edgeDetector
   * @param lineDetector
   * @param cameraConfig
   * @param matrix
   * @param collector
   */
  public LaneDetector(EdgeDetector edgeDetector, LineDetector lineDetector,
      CameraConfig cameraConfig, CameraMatrix matrix,
      ImageCollector collector) {
    this.setEdgeDetector(edgeDetector);
    this.setLineDetector(lineDetector);
    this.setCameraConfig(cameraConfig);
    this.setMatrix(matrix);
    this.setCollector(collector);
  }

  /**
   * detect the lane for the given image
   * @param image
   * @return - the laneDetection result
   */
  public LaneDetectionResult detect(Image image) {
    ImageLaneDetection laneDetect = new ImageLaneDetection(this);
    return laneDetect.detectLane(image, getCollector());
  }

  /**
   * @return the matrix
   */
  public CameraMatrix getMatrix() {
    return matrix;
  }

  /**
   * @param matrix
   *          the matrix to set
   */
  public void setMatrix(CameraMatrix matrix) {
    this.matrix = matrix;
  }

  public CameraConfig getCameraConfig() {
    return cameraConfig;
  }

  public void setCameraConfig(CameraConfig cameraConfig) {
    this.cameraConfig = cameraConfig;
  }

  /**
   * @return the edgeDetector
   */
  public EdgeDetector getEdgeDetector() {
    return edgeDetector;
  }

  /**
   * @param edgeDetector
   *          the edgeDetector to set
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
   * @param lineDetector
   *          the lineDetector to set
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
   * @param collector
   *          the collector to set
   */
  public void setCollector(ImageCollector collector) {
    this.collector = collector;
  }

}
