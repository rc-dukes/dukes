package org.rcdukes.video;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * denoise an image by blurring it
 *
 */
public class DenoiseByBlur {

    private int kWidth = 3;
    private int kHeight = 3;

    public DenoiseByBlur() {}

    /**
     * construct me with given widt and height parameters
     * @param kWidth
     * @param kHeight
     */
    public DenoiseByBlur(int kWidth, int kHeight) {
        this();
        this.kWidth = kWidth;
        this.kHeight = kHeight;
    }

    /**
     * apply my 
     * @param image
     * @return
     */
    public Mat denoise(Mat image) {
        Imgproc.blur(image, image, new Size(kWidth, kHeight));
        return image;
    }

}
