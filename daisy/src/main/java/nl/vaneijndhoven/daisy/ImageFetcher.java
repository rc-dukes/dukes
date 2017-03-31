package nl.vaneijndhoven.daisy;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class ImageFetcher {

    private VideoCapture capture = new VideoCapture();

    public ImageFetcher(String source) {
        this.capture.open(source);
    }

    public Mat fetch() {
        if (this.capture.isOpened()) {
            throw new IllegalStateException("Trying to fetch image from unopened VideoCapture");
        }

        final Mat frame = new Mat();

        this.capture.read(frame);

        return !frame.empty() ? frame : null;
    }
}
