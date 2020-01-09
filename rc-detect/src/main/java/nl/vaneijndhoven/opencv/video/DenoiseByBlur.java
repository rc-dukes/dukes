package nl.vaneijndhoven.opencv.video;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class DenoiseByBlur {

    private int kWidth = 3;
    private int kHeight = 3;

    public DenoiseByBlur() {}

    public DenoiseByBlur(int kWidth, int kHeight) {
        this();
        this.kWidth = kWidth;
        this.kHeight = kHeight;
    }

    public Mat denoise(Mat image) {
        Imgproc.blur(image, image, new Size(kWidth, kHeight));
        return image;
    }

}
