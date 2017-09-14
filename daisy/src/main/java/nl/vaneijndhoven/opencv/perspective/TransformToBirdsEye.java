package nl.vaneijndhoven.opencv.perspective;

import nl.vaneijndhoven.geometry.Polygon;
import nl.vaneijndhoven.opencv.mapper.PointMapper;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.List;
import java.util.stream.Collectors;


public class TransformToBirdsEye {

    private final Polygon imagePolygon;
    private final Polygon worldPolygon;
    Mat perspectiveTransform;

    public TransformToBirdsEye(Polygon imagePolygon, Polygon worldPolygon) {
        this.imagePolygon = imagePolygon;
        this.worldPolygon = worldPolygon;
        this.perspectiveTransform = buildPerspectiveTransform();
    }

    private Mat buildPerspectiveTransform() {

        List<Point> imagePoints = this.imagePolygon.getPointsCounterClockwise().stream().map(PointMapper::toPoint).collect(Collectors.toList());
        List<Point> worldPoints = this.worldPolygon.getPointsCounterClockwise().stream().map(PointMapper::toPoint).collect(Collectors.toList());

        MatOfPoint2f imagePointsMat = new MatOfPoint2f();
        MatOfPoint2f worldPointsMat = new MatOfPoint2f();

        imagePointsMat.fromList(imagePoints);
        worldPointsMat.fromList(worldPoints);

        return Imgproc.getPerspectiveTransform(imagePointsMat, worldPointsMat);
    }

    public Mat transform(Mat input) {
        Mat output = new Mat();
        Imgproc.warpPerspective(input, output, perspectiveTransform, input.size());
        return output;
    }
}
