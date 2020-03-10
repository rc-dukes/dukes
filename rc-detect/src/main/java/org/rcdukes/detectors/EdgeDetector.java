package org.rcdukes.detectors;

import org.opencv.core.Mat;

/**
 *  detector for edges
 */
public interface EdgeDetector {
    Mat detect(Mat image);
}
