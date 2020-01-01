package nl.vaneijndhoven.opencv.tools;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import static nl.vaneijndhoven.opencv.tools.MemoryManagement.closable;

/**
 * a set of images to be used for display/debugging
 */
public class ImageCollector {

    private byte[] imgEdges;
    private byte[] imgLines;
    private byte[] imgStartLight;
    private byte[] imgMask;
    private byte[] imgMorph;
    private byte[] imgOriginalFrame;

    public void edges(Mat image) {
        imgEdges = mat2byteArray(image);
    }

    public void lines(Mat lines) {
        imgLines = mat2byteArray(lines);
    }

    public void startLight(Mat frame) {
        imgStartLight = mat2byteArray(frame);
    }

    public void mask(Mat mask) {
        imgMask = mat2byteArray(mask);
    }

    public void morph(Mat morphOutput) {
        imgMorph = mat2byteArray(morphOutput);
    }

    public void originalFrame(Mat frame) {
        imgOriginalFrame = mat2byteArray(frame);
    }

    private byte[] mat2byteArray(Mat image) {
        if (image.size().height == 0 || image.size().width == 0) {
            return null;
        }

        try (MemoryManagement.ClosableMat<MatOfByte> buffer = closable(new MatOfByte())) {
            Imgcodecs.imencode(".png", image, buffer.get());
            return buffer.get().toArray();
        }
    }

    public byte[] edges() {
        return imgEdges;
    }

    public byte[] lines() {
        return imgLines;
    }

    public byte[] startLight() {
        return imgStartLight;
    }

    public byte[] mask() {
        return imgMask;
    }

    public byte[] morph() {
        return imgMorph;
    }

    public byte[] originalFrame() {
        return imgOriginalFrame;
    }

}
