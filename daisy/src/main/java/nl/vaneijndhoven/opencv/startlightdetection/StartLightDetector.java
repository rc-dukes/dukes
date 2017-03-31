package nl.vaneijndhoven.opencv.startlightdetection;

import nl.vaneijndhoven.objects.StartLight;
import nl.vaneijndhoven.opencv.tools.ImageCollector;
import org.opencv.core.Mat;

public interface StartLightDetector {

    StartLight detect(Mat image);

    StartLightDetector withImageCollector(ImageCollector imageCollector);
}
