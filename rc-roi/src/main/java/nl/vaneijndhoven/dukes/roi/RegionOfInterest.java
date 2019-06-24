package nl.vaneijndhoven.dukes.roi;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;

/**
 * a region of interest relative to the screen
 *
 */
public class RegionOfInterest {

    private final double xFraction;
    private final double yFraction;
    private final double widthFraction;
    private final double heightFraction;

    public double getxFraction() {
      return xFraction;
    }

    public double getyFraction() {
      return yFraction;
    }

    public double getWidthFraction() {
      return widthFraction;
    }

    public double getHeightFraction() {
      return heightFraction;
    }

    /**
     * construct me with the given fractions
     * @param xFraction x
     * @param yFraction y
     * @param widthFraction width
     * @param heightFraction height
     */
    public RegionOfInterest(double xFraction, double yFraction, double widthFraction, double heightFraction) {
        this.xFraction = xFraction;
        this.yFraction = yFraction;
        this.widthFraction = widthFraction;
        this.heightFraction = heightFraction;
    }

    /**
     * create a rectangle based on the given OpenCV Size
     * @param base - the base size to use
     * @return - the rectangle
     */
    public Rect roi(Size base) {
        double roiX = base.height * xFraction;
        double roiY = base.height * yFraction;

        double roiHeight = base.height * heightFraction;
        double roiWidth = base.width * widthFraction;

        Point origin = new Point(roiX, roiY);
        Size roiSize = new Size(roiWidth, roiHeight);

        return new Rect(origin, roiSize);
    }

    /**
     * construct the region of interest image from the given base image
     * @param image
     * @return the region of interest image
     */
    public Mat region(Mat image) {
        // Use 2-arg constructor to create region image backed by original image data
        return new Mat(image, roi(image.size()));
    }

}
