package nl.vaneijndhoven.opencv.lanedetection;

import nl.vaneijndhoven.dukes.geometry.Line;
import nl.vaneijndhoven.objects.Lane;
import nl.vaneijndhoven.objects.ViewPort;

import java.util.Collection;

public interface LaneDetector {

    Lane detect(Collection<Line> lines, ViewPort viewPort);

}
