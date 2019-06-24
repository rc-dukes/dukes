package nl.vaneijndhoven.opencv.stopzonedetection;

import nl.vaneijndhoven.dukes.geometry.Line;
import nl.vaneijndhoven.objects.StoppingZone;
import nl.vaneijndhoven.objects.stoppingzone.ZoneEndBoundary;
import nl.vaneijndhoven.objects.stoppingzone.ZoneStartBoundary;
import java.util.Collection;
import java.util.Optional;

public class DefaultStoppingZoneDetector implements StoppingZoneDetector {


    @Override
    public StoppingZone detect(Collection<Line> lines) {
        Optional<Line> endLine = new ZoneEndBoundary().boundary(lines);
        Optional<Line> startLine = new ZoneStartBoundary().boundary(lines);

        StoppingZone stoppingZone = new StoppingZone(startLine, endLine);
        return stoppingZone;
    }
}
