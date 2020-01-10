package org.rcdukes.camera;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import nl.vaneijndhoven.dukes.geometry.Polygon;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * perspective shift
 */
public class PerspectiveShift implements UnaryOperator<Mat> {

    private final Polygon imagePolygon;
    private final Polygon worldPolygon;
    Mat perspectiveTransform;

    /**
     * construct me from the given image and worl polygon
     * @param imagePolygon - image
     * @param worldPolygon - world
     */
    public PerspectiveShift(Polygon imagePolygon, Polygon worldPolygon) {
        this.imagePolygon = imagePolygon;
        this.worldPolygon = worldPolygon;
        this.perspectiveTransform = buildPerspectiveTransform();
    }

    private Mat buildPerspectiveTransform() {

        List<Point> imagePoints = this.imagePolygon.getPointsCounterClockwise().stream().map(point -> new Point(point.getX(), point.getY())).collect(Collectors.toList());
        List<Point> worldPoints = this.worldPolygon.getPointsCounterClockwise().stream().map(point -> new Point(point.getX(), point.getY())).collect(Collectors.toList());

        MatOfPoint2f imagePointsMat = new MatOfPoint2f();
        MatOfPoint2f worldPointsMat = new MatOfPoint2f();

        imagePointsMat.fromList(imagePoints);
        worldPointsMat.fromList(worldPoints);

        return Imgproc.getPerspectiveTransform(imagePointsMat, worldPointsMat);
    }

    @Override
    public Mat apply(Mat input) {
        Mat output = new Mat();
        Imgproc.warpPerspective(input, output, perspectiveTransform, input.size());
        return output;
    }
}
