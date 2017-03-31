package nl.vaneijndhoven.opencv.objectdetection;

import nl.vaneijndhoven.geometry.Line;
import nl.vaneijndhoven.opencv.edgedectection.EdgeDetector;
import nl.vaneijndhoven.opencv.linedetection.LineDetector;
import org.opencv.core.Mat;

import java.util.Collection;

public class LineExtractor {

    private final EdgeDetector edgeDetector;
    private final LineDetector lineDetector;

    public LineExtractor(EdgeDetector edgeDetector, LineDetector lineDetector) {
        this.edgeDetector = edgeDetector;
        this.lineDetector = lineDetector;
    }

    public Collection<Line> extract(Mat image) {
        // step1 edge detection
        Mat imgEdges = edgeDetector.detect(image);

        // step 2 line detection
        return lineDetector.detect(imgEdges);
    }
}
