package nl.vaneijndhoven.opencv.stopzonedetection;

import org.rcdukes.geometry.Line;
import nl.vaneijndhoven.objects.StoppingZone;

import java.util.Collection;

public interface StoppingZoneDetector {

    StoppingZone detect(Collection<Line> lines);
}
