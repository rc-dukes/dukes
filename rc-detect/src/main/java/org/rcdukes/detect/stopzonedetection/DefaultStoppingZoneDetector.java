package org.rcdukes.detect.stopzonedetection;

import org.rcdukes.geometry.Line;
import org.rcdukes.objects.StoppingZone;
import org.rcdukes.objects.stoppingzone.ZoneEndBoundary;
import org.rcdukes.objects.stoppingzone.ZoneStartBoundary;

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
