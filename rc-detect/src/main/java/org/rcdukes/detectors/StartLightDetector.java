package org.rcdukes.detectors;

import org.rcdukes.objects.StartLight;
import org.rcdukes.video.Image;
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
    StartLight detect(Image image);

    StartLightDetector withImageCollector(ImageCollector imageCollector);
}
