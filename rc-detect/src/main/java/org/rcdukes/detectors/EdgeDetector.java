package org.rcdukes.detectors;

import org.opencv.core.Mat;

/**
 *  detector or edges
 */
public interface EdgeDetector {
    Mat detect(Mat image);
}
