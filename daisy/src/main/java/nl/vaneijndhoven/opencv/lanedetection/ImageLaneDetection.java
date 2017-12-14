package nl.vaneijndhoven.opencv.lanedetection;

import nl.vaneijndhoven.daisy.Daisy;
import nl.vaneijndhoven.geometry.Line;
import nl.vaneijndhoven.geometry.Point;
import nl.vaneijndhoven.geometry.Polygon;
import nl.vaneijndhoven.navigation.plot.LaneOrientation;
import nl.vaneijndhoven.navigation.plot.StoppingZoneOrientation;
import nl.vaneijndhoven.objects.*;
import nl.vaneijndhoven.opencv.edgedectection.CannyEdgeDetector;
import nl.vaneijndhoven.opencv.linedetection.ProbabilisticHoughLinesLineDetector;
import nl.vaneijndhoven.opencv.objectdetection.LineExtractor;
import nl.vaneijndhoven.opencv.perspective.TransformToBirdsEye;
import nl.vaneijndhoven.opencv.roi.RegionOfInterest;
import nl.vaneijndhoven.opencv.stopzonedetection.DefaultStoppingZoneDetector;
import nl.vaneijndhoven.opencv.tools.ImageCollector;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.*;

import static java.util.Arrays.asList;
import static nl.vaneijndhoven.opencv.mapper.PointMapper.toPoint;

public class ImageLaneDetection {

    private final CannyEdgeDetector.Config cannyConfig;
    private final ProbabilisticHoughLinesLineDetector.Config lineDetectorConfig;

    public ImageLaneDetection(CannyEdgeDetector.Config cannyConfig, ProbabilisticHoughLinesLineDetector.Config lineDetectorConfig) {
        this.cannyConfig = cannyConfig;
        this.lineDetectorConfig = lineDetectorConfig;
    }

    public Map<String, Object> detectLane(Mat original, ImageCollector imageCollector) {
        if (original.empty()) {
            System.err.println("detectLane: empty mat?");
        }

        Mat image = new RegionOfInterest(0, 0.45, 1, 0.55).region(original);
        Size imageSize = image.size();
        ViewPort viewPort = new ViewPort(new Point(0, 0), imageSize.width, imageSize.height);

        Polygon imagePolygon = new Polygon(
                new Point(0.45 * imageSize.width, 0.1 * imageSize.height),
                new Point(0.55 * imageSize.width, 0.1 * imageSize.height),
                new Point(0.9 * imageSize.width, imageSize.height),
                new Point(0.1 * imageSize.width, imageSize.height));

        Polygon worldPolygon = new Polygon(
                new Point(0.3 * imageSize.width, 0),
                new Point(0.7 * imageSize.width, 0),
                new Point(0.7 * imageSize.width, imageSize.height),
                new Point(0.3 * imageSize.width, imageSize.height));

        Daisy.BIRDS_EYE = new TransformToBirdsEye(imagePolygon, worldPolygon).transform(image);

        LineExtractor lineExtractor = new LineExtractor(
                new CannyEdgeDetector(cannyConfig).withImageCollector(imageCollector),
                new ProbabilisticHoughLinesLineDetector(lineDetectorConfig).withImageCollector(imageCollector)
        );

        Collection<Line> lines = lineExtractor.extract(image);

        Lane lane = new DefaultLaneDetector().detect(lines, viewPort);
        StoppingZone stoppingZone = new DefaultStoppingZoneDetector().detect(lines);

        LaneOrientation laneOrientation = new LaneOrientation(lane, viewPort);
        StoppingZoneOrientation stoppingZoneOrientation = new StoppingZoneOrientation(stoppingZone, lane, viewPort);

        Optional<Line> middle = laneOrientation.determineLaneMiddle();

        lane.getLeftBoundary().ifPresent(boundary -> drawLinesToImage(image, asList(boundary), new Scalar(0, 255, 0)));
        lane.getRightBoundary().ifPresent(boundary -> drawLinesToImage(image, asList(boundary), new Scalar(255, 128, 0)));
        middle.ifPresent(line -> drawLinesToImage(image, asList(line), new Scalar(0, 0, 255)));

        double distanceToStoppingZone = -1;
        double distanceToStoppingZoneEnd = -1;
        if (stoppingZone.getEntrance() != null) {
            stoppingZone.getEntrance().ifPresent(entrance -> drawLinesToImage(image, asList(entrance), new Scalar(255, 255, 0)));
            distanceToStoppingZone = stoppingZoneOrientation.determineDistanceToStoppingZone();
        }

        if (stoppingZone.getExit() != null) {
            stoppingZone.getExit().ifPresent(exit -> drawLinesToImage(image, asList(exit), new Scalar(0, 255, 255)));
            distanceToStoppingZoneEnd = stoppingZoneOrientation.determineDistanceToStoppingZoneEnd();
        }

        imageCollector.lines(image);

        double angle = laneOrientation.determineCurrentAngle();

        double distanceMiddle = laneOrientation.determineDistanceToMiddle();
        double distanceLeft = laneOrientation.distanceFromLeftBoundary();
        double distanceRight = laneOrientation.distanceFromRightBoundary();

        double courseRelativeToHorizon = laneOrientation.determineCourseRelativeToHorizon();

        Map result = new HashMap<>();
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

    private void putIfNumber(String key, double angle, Map result) {
        if (Double.isNaN(angle)) {
            return;
        }

        result.put(key, angle);
    }

    private void drawLinesToImage(Mat image, Collection<Line> lines, Scalar color) {
        lines.stream().filter(Objects::nonNull).forEach(line -> Imgproc.line(image, toPoint(line.getPoint1()), toPoint(line.getPoint2()), color, 4));
    }

}