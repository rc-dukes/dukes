package nl.vaneijndhoven.opencv.roi;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;

public class RegionOfInterest {

    private final double xFraction;
    private final double yFraction;
    private final double widthFraction;
    private final double heightFraction;

    public RegionOfInterest(double xFraction, double yFraction, double widthFraction, double heightFraction) {
        this.xFraction = xFraction;
        this.yFraction = yFraction;
        this.widthFraction = widthFraction;
        this.heightFraction = heightFraction;
    }

    public Rect roi(Size base) {
        double roiX = base.height * xFraction;
        double roiY = base.height * yFraction;

        double roiHeight = base.height * heightFraction;
        double roiWidth = base.width * widthFraction;

        Point origin = new Point(roiX, roiY);
        Size roiSize = new Size(roiWidth, roiHeight);

        return new Rect(origin, roiSize);
    }

    public Mat region(Mat image) {
        // Use 2-arg constructor to create region image backed by original image data
        return new Mat(image, roi(image.size()));
    }

}
