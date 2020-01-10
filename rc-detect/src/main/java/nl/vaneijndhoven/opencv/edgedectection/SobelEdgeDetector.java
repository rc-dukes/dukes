package nl.vaneijndhoven.opencv.edgedectection;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.rcdukes.detectors.EdgeDetector;

import static org.opencv.core.CvType.CV_8UC1;

public class SobelEdgeDetector implements EdgeDetector {

    int ddepth = CV_8UC1;
    int dx = 2;
    int dy = 2;

    public SobelEdgeDetector() {}

    public SobelEdgeDetector(int ddepth, int dx, int dy) {
        this.ddepth = ddepth;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public Mat detect(Mat image) {
        Mat imgEdges = new Mat();
        Imgproc.Sobel(image, imgEdges, ddepth, dx, dy);
        return imgEdges;
    }
}
