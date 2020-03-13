package org.rcdukes.detect.stopzonedetection;

import org.rcdukes.geometry.Line;
import org.rcdukes.objects.StoppingZone;

import java.util.Collection;

/**
 * detector for stopping zone
 */
public interface StoppingZoneDetector {

    StoppingZone detect(Collection<Line> lines);
}
