package nl.vaneijndhoven.dukes.cooter;

import nl.vaneijndhoven.dukes.cooter.CameraMatrix;
import org.junit.Ignore;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.*;

@Ignore
public class CameraMatrixTest {
    @Test
    public void calc() throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        CameraMatrix matrix = new CameraMatrix(8, 6);

        ArrayList<Mat> images = new ArrayList<>();
        Files.newDirectoryStream(Paths.get("/Users/jpoint/Repositories/dukes/cooter/target/test-classes/cameramatrix"),
                path ->
                        path.getFileName().toString().startsWith("GOPR"))
                .forEach(path -> {
                    System.out.println("reading: " + path);
            String substring = path.toString();
            Mat image = Imgcodecs.imread(substring);
            matrix.calibrate(image);
        });

        images.forEach(Mat::release);

        System.out.println(matrix.serialize());

        CameraMatrix deserializedMatrix = CameraMatrix.deserizalize(matrix.serialize());

        Mat image = Imgcodecs.imread("/Users/jpoint/Repositories/dukes/cooter/target/test-classes/cameramatrix/test_image.jpg");

        assertEquals(matrix.serialize(), deserializedMatrix.serialize());

        Mat undistorted = deserializedMatrix.apply(image);

        System.out.println(deserializedMatrix.serialize());

        Imgcodecs.imwrite("/Users/jpoint/Repositories/dukes/cooter/target/test-classes/cameramatrix/debug.jpg", undistorted);

    }

}