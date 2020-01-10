package org.rcdukes.detectors;

import nl.vaneijndhoven.objects.StartLight;

import org.opencv.core.Mat;
import org.rcdukes.video.ImageCollector;

/**
 * start light detection
 */
public interface StartLightDetector {
    /**
     * detect a StartLight from the given image
     * @param image
     * @return the StartLight
     */
    StartLight detect(Mat image);

    StartLightDetector withImageCollector(ImageCollector imageCollector);
}
