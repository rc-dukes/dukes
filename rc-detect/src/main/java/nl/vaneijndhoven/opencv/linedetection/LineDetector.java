package nl.vaneijndhoven.opencv.linedetection;

import org.opencv.core.Mat;

import nl.vaneijndhoven.dukes.geometry.Line;

import java.util.Collection;

public interface LineDetector {

    Collection<Line> detect(Mat image);

}
