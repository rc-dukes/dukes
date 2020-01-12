package org.rcdukes.detect.lanedetection;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.rcdukes.camera.ImagePolygon;
import org.rcdukes.camera.PerspectiveShift;
import org.rcdukes.detect.CameraConfig;
import org.rcdukes.detect.LaneDetector;
import org.rcdukes.geometry.Line;
import org.rcdukes.geometry.Point;
import org.rcdukes.geometry.Polygon;
import org.rcdukes.roi.ROI;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageCollector;
import org.rcdukes.video.ImageCollector.ImageType;
import org.rcdukes.video.ImageUtils;
import org.rcdukes.video.ImageUtils.CVColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vaneijndhoven.navigation.plot.LaneOrientation;
import nl.vaneijndhoven.navigation.plot.StoppingZoneOrientation;
import nl.vaneijndhoven.objects.Lane;
import nl.vaneijndhoven.objects.StoppingZone;
import nl.vaneijndhoven.objects.ViewPort;
import nl.vaneijndhoven.opencv.stopzonedetection.DefaultStoppingZoneDetector;

/**
 * image lane detection
 *
 */
public class ImageLaneDetection {
  protected static final Logger LOG = LoggerFactory
      .getLogger(ImageLaneDetection.class);
  private LaneDetector ld;

  /**
   * create me for the givne LaneDetector
   * @param laneDetector
   */
  public ImageLaneDetection(LaneDetector laneDetector) {
    this.ld = laneDetector;
  }

  /**
   * detect the lane
   * @param image
   * @param imageCollector
   * @return a map with information
   */
  public Map<String, Object> detectLane(Image image,
      ImageCollector imageCollector) {
    Map<String, Object> result = new HashMap<String, Object>();
    if (image==null) {
      LOG.error("detectLane: original is null");
      return result;
    }
    CameraConfig cameraConfig=ld.getCameraConfig();
    imageCollector.addImage(image, ImageType.camera);
    // ! important - create a copy of the image because debug info will be
    // written on it
    Mat imageCopy=image.getFrame().clone(); 
    Mat undistorted = ld.getMatrix().apply(imageCopy);
    // get the configured Region of interest
    Mat frame = new ROI("camera",0, cameraConfig.getRoiw(), 1, cameraConfig.getRoih())
        .region(undistorted);
    Size imageSize = frame.size();
    ViewPort viewPort = new ViewPort(new Point(0, 0), imageSize.width,
        imageSize.height);
    Polygon imagePolygon = new ImagePolygon(frame.size(), 0, 0, 1, 0, 1, 1, 0, 1);

    Polygon worldPolygon = new ImagePolygon(frame.size(), 0, 0, 1, 0, 1, 1, 0, 1);

    PerspectiveShift perspectiveShift = new PerspectiveShift(imagePolygon,
        worldPolygon);
    Mat birdseye = perspectiveShift.apply(frame);
    imageCollector.addImage(birdseye, ImageType.birdseye);

    // step1 edge detection
    Mat imgEdges = ld.getEdgeDetector().detect(frame);
    imageCollector.addImage(imgEdges, ImageType.edges);

    // step 2 line detection
    Collection<Line> lines = ld.getLineDetector().detect(imgEdges);
    Lane lane = new DefaultLaneDetector().detect(lines, viewPort);
    StoppingZone stoppingZone = new DefaultStoppingZoneDetector().detect(lines);

    LaneOrientation laneOrientation = new LaneOrientation(lane, viewPort);
    StoppingZoneOrientation stoppingZoneOrientation = new StoppingZoneOrientation(
        stoppingZone, lane, viewPort);

    Optional<Line> middle = laneOrientation.determineLaneMiddle();

    ImageUtils iu=new ImageUtils();
    lane.getLeftBoundary().ifPresent(boundary -> iu.drawLinesToImage(frame,
        asList(boundary), CVColor.green));
    lane.getRightBoundary().ifPresent(boundary -> iu.drawLinesToImage(frame,
        asList(boundary), CVColor.dodgerblue));
    middle.ifPresent(
        line -> iu.drawLinesToImage(frame, asList(line), CVColor.red));

    double distanceToStoppingZone = -1;
    double distanceToStoppingZoneEnd = -1;
    if (stoppingZone.getEntrance() != null) {
      stoppingZone.getEntrance().ifPresent(entrance -> iu.drawLinesToImage(frame,
          asList(entrance), CVColor.cyan));
      distanceToStoppingZone = stoppingZoneOrientation
          .determineDistanceToStoppingZone();
    }

    if (stoppingZone.getExit() != null) {
      stoppingZone.getExit().ifPresent(exit -> iu.drawLinesToImage(frame,
          asList(exit), CVColor.yellow));
      distanceToStoppingZoneEnd = stoppingZoneOrientation
          .determineDistanceToStoppingZoneEnd();
    }
    imageCollector.addImage(frame, ImageType.lines);
    

    double angle = laneOrientation.determineCurrentAngle();

    double distanceMiddle = laneOrientation.determineDistanceToMiddle();
    double distanceLeft = laneOrientation.distanceFromLeftBoundary();
    double distanceRight = laneOrientation.distanceFromRightBoundary();

    double courseRelativeToHorizon = laneOrientation
        .determineCourseRelativeToHorizon();

    result.put("lane", lane);
    putIfNumber("angle", angle, result);
    putIfNumber("distanceMiddle", distanceMiddle, result);
    putIfNumber("distanceLeft", distanceLeft, result);
    putIfNumber("distanceRight", distanceRight, result);
    putIfNumber("distanceToStoppingZone", distanceToStoppingZone, result);
    putIfNumber("distanceToStoppingZoneEnd", distanceToStoppingZoneEnd, result);
    putIfNumber("courseRelativeToHorizon", courseRelativeToHorizon, result);

    return result;
  }

  private void putIfNumber(String key, double angle,
      Map<String, Object> result) {
    if (Double.isNaN(angle)) {
      return;
    }
    result.put(key, angle);
  }

  

}