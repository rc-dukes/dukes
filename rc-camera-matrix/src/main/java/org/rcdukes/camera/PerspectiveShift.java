package org.rcdukes.camera;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.rcdukes.geometry.Polygon;

/**
 * perspective shift
 */
public class PerspectiveShift implements UnaryOperator<Mat> {

    final Polygon imagePolygon;
    final Polygon worldPolygon;
    List<Point> imagePoints;
    List<Point> worldPoints;
    Mat perspectiveTransform;

    /**
     * construct me from the given image and worl polygon
     * @param imagePolygon - image
     * @param worldPolygon - world
     */
    public PerspectiveShift(Polygon imagePolygon, Polygon worldPolygon) {
        this.imagePolygon = imagePolygon;
        this.worldPolygon = worldPolygon;
        buildPerspectiveTransform();
    }

    /**
     * create the perspective transform
     */
    private void buildPerspectiveTransform() {

        imagePoints = this.imagePolygon.getPointsCounterClockwise().stream().map(point -> new Point(point.getX(), point.getY())).collect(Collectors.toList());
        worldPoints = this.worldPolygon.getPointsCounterClockwise().stream().map(point -> new Point(point.getX(), point.getY())).collect(Collectors.toList());

        MatOfPoint2f imagePointsMat = new MatOfPoint2f();
        MatOfPoint2f worldPointsMat = new MatOfPoint2f();

        imagePointsMat.fromList(imagePoints);
        worldPointsMat.fromList(worldPoints);

        this.perspectiveTransform =  Imgproc.getPerspectiveTransform(worldPointsMat, imagePointsMat);
    }

    @Override
    public Mat apply(Mat srcImage) {
        Mat destImage = new Mat();
        Imgproc.warpPerspective(srcImage, destImage, perspectiveTransform, srcImage.size());
        return destImage;
    }
}
