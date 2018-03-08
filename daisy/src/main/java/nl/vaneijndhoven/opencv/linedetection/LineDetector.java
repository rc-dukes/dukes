package nl.vaneijndhoven.opencv.linedetection;

import nl.vaneijndhoven.dukes.enos.geometry.Line;
import org.opencv.core.Mat;

import java.util.Collection;

public interface LineDetector {

    Collection<Line> detect(Mat image);

}
