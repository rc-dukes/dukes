package nl.vaneijndhoven.opencv.tools;

import nl.vaneijndhoven.geometry.Line;
import nl.vaneijndhoven.objects.Lane;
import nl.vaneijndhoven.objects.StoppingZone;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.Collection;
import java.util.Objects;

import static nl.vaneijndhoven.opencv.mapper.PointMapper.toPoint;

public class DebugTools {

    private String path;
    private String filePrefix;

    public DebugTools(String path, String filePrefix) {
        this.path = path;
        this.filePrefix = filePrefix;
    }

    public void output(Mat img, String name) {
        String fileName = filePrefix.replace(".", "-" + name + ".");
        Imgcodecs.imwrite(path + fileName, img);
    }

    public void output(Mat img, Collection<Line> lines, String name, Scalar color) {
        String fileName = filePrefix.replace(".", "-" + name + ".");
        Mat output = new Mat();
        img.copyTo(output);
        lines.stream().filter(Objects::nonNull).forEach(line -> Imgproc.line(output, toPoint(line.getPoint1()), toPoint(line.getPoint2()), color, 4));
        Imgcodecs.imwrite(path + fileName, output);
        output.release();
    }

//    public void output(Mat img, Lane lane, String name, Scalar color) {
//        String fileName = filePrefix.replace(".", "-" + name + ".");
//        Mat output = new Mat();
//        img.copyTo(output);
//
//        if (lane.getLeftBoundary() != null) {
//            Imgproc.line(output, toPoint(lane.getLeftBoundary().getPoint1()), toPoint(lane.getLeftBoundary().getPoint2()), color, 4);
//        }
//
//        if (lane.getRightBoundary() != null) {
//            Imgproc.line(output, toPoint(lane.getRightBoundary().getPoint1()), toPoint(lane.getRightBoundary().getPoint2()), color, 4);
//        }
//
//        Imgcodecs.imwrite(path + fileName, output);
//        output.release();
//    }

//    public void output(Mat img, StoppingZone stoppingZone, String name, Scalar color) {
//        String fileName = filePrefix.replace(".", "-" + name + ".");
//        Mat output = new Mat();
//        img.copyTo(output);
//
//        if (stoppingZone.getEntrance() != null) {
//            Imgproc.line(output, toPoint(stoppingZone.getEntrance().getPoint1()), toPoint(stoppingZone.getEntrance().getPoint2()), color, 4);
//        }
//
//        if (stoppingZone.getExit() != null) {
//            Imgproc.line(output, toPoint(stoppingZone.getExit().getPoint1()), toPoint(stoppingZone.getExit().getPoint2()), color, 4);
//        }
//
//        Imgcodecs.imwrite(path + fileName, output);
//        output.release();
//    }

    public static class VoidTools extends DebugTools {

        public VoidTools() {
            super(null, null);
        }

        @Override
        public void output(Mat img, String name) {
            // do nothing
        }

        @Override
        public void output(Mat img, Collection<Line> lines, String name, Scalar color) {
            // do nothing
        }

//        @Override
//        public void output(Mat img, Lane lane, String name, Scalar color) {
//            // do nothing
//        }
//
//        @Override
//        public void output(Mat img, StoppingZone stoppingZone, String name, Scalar color) {
//            // do nothing
//        }

    }

}
