package org.rcdukes.detect.lanedetection;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.Optional;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.rcdukes.camera.ImagePolygon;
import org.rcdukes.camera.PerspectiveShift;
import org.rcdukes.detect.CameraConfig;
import org.rcdukes.detect.LaneDetector;
import org.rcdukes.detect.stopzonedetection.DefaultStoppingZoneDetector;
import org.rcdukes.geometry.Lane;
import org.rcdukes.geometry.LaneDetectionResult;
import org.rcdukes.geometry.Line;
import org.rcdukes.geometry.Point;
import org.rcdukes.geometry.Polygon;
import org.rcdukes.objects.StoppingZone;
import org.rcdukes.objects.ViewPort;
import org.rcdukes.objects.stoppingzone.StoppingZoneOrientation;
import org.rcdukes.roi.ROI;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageCollector;
import org.rcdukes.video.ImageCollector.ImageType;
import org.rcdukes.video.ImageUtils;
import org.rcdukes.video.ImageUtils.CVColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * image lane detection
 *
 */
public class ImageLaneDetection {
  protected static final Logger LOG = LoggerFactory
      .getLogger(ImageLaneDetection.class);
  private LaneDetector ld;

  /**
   * create me for the given LaneDetector
   * 
   * @param laneDetector
   */
  public ImageLaneDetection(LaneDetector laneDetector) {
    this.ld = laneDetector;
  }

  /**
   * detect the lane
   * 
   * @param image
   * @param imageCollector
   * @return a map with information
   */
  public LaneDetectionResult detectLane(Image image,
      ImageCollector imageCollector) {
    LaneDetectionResult ldr = new LaneDetectionResult();
    if (image == null) {
      LOG.error("detectLane: original is null");
      return ldr;
    }
    CameraConfig cameraConfig = ld.getCameraConfig();
    imageCollector.addImage(image, ImageType.camera);
    ldr.frameIndex = image.getFrameIndex();
    ldr.milliTimeStamp = image.getMilliTimeStamp();
    // ! important - create a copy of the image because debug info might be
    // written on it
    Mat imageCopy = image.getFrame().clone();
    Mat undistorted = ld.getMatrix().apply(imageCopy);
    // get the configured Region of interest
    double ry = cameraConfig.getRoiy() / 100.0;
    double rh = (1 - ry) * cameraConfig.getRoih() / 100.0;
    Mat frame = new ROI("camera", 0, ry, 1, rh).region(undistorted);
    Size imageSize = frame.size();
    ViewPort viewPort = new ViewPort(new Point(0.0, 0.0), imageSize.width,
        imageSize.height);
    Polygon imagePolygon = new ImagePolygon(frame.size(), 0, 0, 1, 0, 1, 1, 0,
        1);
    Polygon worldPolygon = new ImagePolygon(frame.size(), 0, 0, 1, 0, 1, 1, 0,
        1);

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
    laneOrientation.determineLines();
    StoppingZoneOrientation stoppingZoneOrientation = new StoppingZoneOrientation(
        stoppingZone, lane, viewPort);

    Optional<Line> middle = Optional.ofNullable(laneOrientation.getMiddle());

    ImageUtils iu = new ImageUtils();
    lane.getLeftBoundary().ifPresent(boundary -> iu.drawLinesToImage(frame,
        asList(boundary), CVColor.green));
    lane.getRightBoundary().ifPresent(boundary -> iu.drawLinesToImage(frame,
        asList(boundary), CVColor.dodgerblue));
    middle.ifPresent(
        line -> iu.drawLinesToImage(frame, asList(line), CVColor.red));

    ldr.distanceToStoppingZone = -1.0;
    ldr.distanceToStoppingZoneEnd = -1.0;
    if (stoppingZone.getEntrance() != null) {
      if (cameraConfig.isShowStoppingZone())
        stoppingZone.getEntrance().ifPresent(entrance -> iu
            .drawLinesToImage(frame, asList(entrance), CVColor.cyan));
      ldr.distanceToStoppingZone = stoppingZoneOrientation
          .determineDistanceToStoppingZone();
    }

    if (stoppingZone.getExit() != null) {
      if (cameraConfig.isShowStoppingZone())
        stoppingZone.getExit().ifPresent(
            exit -> iu.drawLinesToImage(frame, asList(exit), CVColor.yellow));
      ldr.distanceToStoppingZoneEnd = stoppingZoneOrientation
          .determineDistanceToStoppingZoneEnd();
    }
    imageCollector.addImage(frame, ImageType.lines);

    Double angleOffset = cameraConfig.getAngleOffset();
    if (laneOrientation.getLeft() != null)
      ldr.left = laneOrientation.getLeft().angleDeg90() + angleOffset;
    if (laneOrientation.getMiddle() != null)
      ldr.middle = laneOrientation.getMiddle().angleDeg90() + angleOffset;
    if (laneOrientation.getRight() != null)
      ldr.right = laneOrientation.getRight().angleDeg90() + angleOffset;
    ldr.distanceMiddle = laneOrientation.determineDistanceToMiddle();
    ldr.distanceLeft = laneOrientation.distanceFromLeftBoundary();
    ldr.distanceRight = laneOrientation.distanceFromRightBoundary();
    ldr.courseRelativeToHorizon = laneOrientation
        .determineCourseRelativeToHorizon();
    return ldr;
  }
}