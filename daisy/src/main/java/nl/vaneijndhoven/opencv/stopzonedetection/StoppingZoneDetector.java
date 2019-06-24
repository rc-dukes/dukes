package nl.vaneijndhoven.opencv.stopzonedetection;

import nl.vaneijndhoven.dukes.geometry.Line;
import nl.vaneijndhoven.objects.StoppingZone;

import java.util.Collection;

public interface StoppingZoneDetector {

    StoppingZone detect(Collection<Line> lines);
}
