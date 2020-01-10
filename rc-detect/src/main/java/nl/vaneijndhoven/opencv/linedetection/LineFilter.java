package nl.vaneijndhoven.opencv.linedetection;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import org.rcdukes.geometry.Line;

import java.util.*;

import static java.lang.Math.atan2;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class LineFilter {

    private final double angle;
    private final double margin;
    private boolean directional;

    /**
     * create me
     * @param angle
     * @param margin
     * @param directional
     */
    public LineFilter(double angle, double margin, boolean directional) {
        this.angle = angle;
        this.margin = margin;
        this.directional = directional;
    }

    public Collection<Line> filter(Mat lines) {
        Set<Line> lineObjects = new HashSet<>();

        for (int x = 0; x < lines.rows(); x++) {
            Line line = new Line(lines.get(x, 0));
            lineObjects.add(line);
        }

        return filter(lineObjects);
    }

    public Collection<Line> filter(Collection<Line> lines) {
        double minDeg = angle - margin;
        double maxDeg = angle + margin;
        double minRad = toRadians(minDeg);
        double maxRad = toRadians(maxDeg);

//        System.out.println("min degrees: " + minDeg);
//        System.out.println("max degrees: " + maxDeg);
//        System.out.println("min radians: " + minRad);
//        System.out.println("max radians: " + maxRad);

        Set<Line> filtered = new HashSet<>();

        for (Line line : lines) {
            double radian = line.angleRad();

            if (radian >= minRad && radian <= maxRad) {
                filtered.add(line);
            }
        }

        return filtered;
    }

    public void debug(Mat lines) {
        System.out.println("debug lines");
        for (int x = 0; x < lines.rows(); x++) {
            double[] data = lines.get(x, 0);
            double x1 = data[0];
            double y1 = data[1];
            double x2 = data[2];
            double y2 = data[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            double radian = atan2(end.y - start.y, end.x - start.x);

            double deg = toDegrees(radian);

            System.out.println("LINE P("+x1+","+y1+") - P("+x2+","+y2+"), radian: " + radian + ", deg: " +deg);
        }
    }
}
