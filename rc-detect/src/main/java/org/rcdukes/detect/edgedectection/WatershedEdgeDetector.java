package org.rcdukes.detect.edgedectection;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.rcdukes.detectors.EdgeDetector;

import static org.opencv.core.CvType.CV_32S;

public class WatershedEdgeDetector implements EdgeDetector {

    private double threshold = 100;
    private double max = 255;
    private int iterations = 1;
    private double dilatedThreshold = 1;
    private double dilatedMax = 1;

    public WatershedEdgeDetector() {}

    public WatershedEdgeDetector(double threshold, double max, int iterations, double dilatedThreshold, double dilatedMax) {
        this();
        this.threshold = threshold;
        this.max = max;
        this.iterations = iterations;
        this.dilatedThreshold = dilatedThreshold;
        this.dilatedMax = dilatedMax;
    }

    @Override
    public Mat detect(Mat image) {
        Mat imgRgb = new Mat();
        Imgproc.cvtColor(image, imgRgb, Imgproc.COLOR_RGBA2RGB);

        Mat imgGray = new Mat();
        Imgproc.cvtColor(imgRgb, imgGray, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.threshold(imgGray, imgGray, threshold, max, Imgproc.THRESH_BINARY);

        Mat imgEroded = new Mat();
        Imgproc.erode(imgGray,imgEroded,new Mat(),new Point(-1,-1),iterations);

        Mat imgDilated = new Mat();
        Imgproc.dilate(imgGray,imgDilated,new Mat(),new Point(-1,-1),iterations);

        Imgproc.threshold(imgDilated,imgDilated,dilatedThreshold, dilatedMax, Imgproc.THRESH_BINARY_INV);
        Mat markerImage = new Mat(imgGray.size(), CvType.CV_8U, new Scalar(0));
        Core.add(imgEroded, imgDilated, markerImage);

        Mat markers = new Mat();
        markerImage.convertTo(markers, CV_32S);

        Imgproc.watershed(imgRgb, markers);
        markerImage.convertTo(markerImage,CvType.CV_8U);

        imgRgb.release();
        imgGray.release();
        imgEroded.release();
        imgDilated.release();
        markers.release();

        return markerImage;
    }
}
