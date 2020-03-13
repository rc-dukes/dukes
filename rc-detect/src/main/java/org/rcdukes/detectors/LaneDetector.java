package org.rcdukes.detectors;

import org.rcdukes.geometry.Lane;
import org.rcdukes.geometry.Line;
import org.rcdukes.objects.ViewPort;

import java.util.Collection;

/**
 * detect for lanes
 *
 */
public interface LaneDetector {
    Lane detect(Collection<Line> lines, ViewPort viewPort);
}
