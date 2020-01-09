package nl.vaneijndhoven.opencv.edgedectection;

import org.opencv.core.Mat;

/**
 *  interface for edge detection
 */
public interface EdgeDetector {
    Mat detect(Mat image);
}
