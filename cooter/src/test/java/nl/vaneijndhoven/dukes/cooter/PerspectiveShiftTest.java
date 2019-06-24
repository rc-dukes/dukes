package nl.vaneijndhoven.dukes.cooter;

import org.junit.Ignore;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import nl.vaneijndhoven.dukes.cletus.roi.RegionOfInterest;
import nl.vaneijndhoven.dukes.geometry.Point;
import nl.vaneijndhoven.dukes.geometry.Polygon;

@Ignore
public class PerspectiveShiftTest {

    @Test
    public void transform() throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        CameraMatrix matrix = new CameraMatrix(8, 6);

//        ArrayList<Mat> images = new ArrayList<>();
//        Files.newDirectoryStream(Paths.get("/Users/jpoint/Repositories/dukes/cooter/target/test-classes/perspectiveshift"),
//                path ->
//                        path.getFileName().toString().startsWith("GOPR"))
//                .forEach(path -> {
//                    System.out.println("reading: " + path);
//                    String substring = path.toString();
//                    Mat image = Imgcodecs.imread(substring);
//                    matrix.calibrate(image);
//                });

//        images.forEach(Mat::release);

        Mat original = Imgcodecs.imread("/Users/jpoint/Repositories/dukes/cooter/target/test-classes/perspectiveshift/screen_shot.png");
        Mat image = new RegionOfInterest(0, 0.55, 1, 0.45).region(original);

        Size imageSize = image.size();

        Polygon imagePolygon = new Polygon(
                new Point(0 * imageSize.width, imageSize.height),
                new Point(1 * imageSize.width, imageSize.height),
                new Point(0 * imageSize.width, 0 * imageSize.height),
                new Point(1 * imageSize.width, 0 * imageSize.height)
        );

        Polygon worldPolygon = new Polygon(
                new Point(0.45 * imageSize.width, imageSize.height),
                new Point(0.55 * imageSize.width, imageSize.height),
                new Point(0 * imageSize.width, 0 * imageSize.height),
                new Point(1 * imageSize.width, 0 * imageSize.height)
        );

        PerspectiveShift perspectiveShift = new PerspectiveShift(imagePolygon, worldPolygon);

        Mat shifted = perspectiveShift.apply(image);

        Imgcodecs.imwrite("/Users/jpoint/Repositories/dukes/cooter/target/test-classes/perspectiveshift/debug.jpg", image);
        Imgcodecs.imwrite("/Users/jpoint/Repositories/dukes/cooter/target/test-classes/perspectiveshift/debug2.jpg", shifted);
    }

}