package nl.vaneijndhoven.opencv.startlightdetection;

import nl.vaneijndhoven.objects.StartLight;
import nl.vaneijndhoven.opencv.tools.ImageCollector;
import org.opencv.core.Mat;

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
