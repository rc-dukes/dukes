package nl.vaneijndhoven.opencv.edgedectection;

import org.opencv.core.Mat;

public interface EdgeDetector {

    Mat detect(Mat image);

}
