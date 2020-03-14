package org.rcdukes.detect;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.rcdukes.video.ColorFilter;
import org.rcdukes.video.DenoiseByBlur;
import org.rcdukes.video.ImageUtils;

/**
 * test the image Processing functions
 */
public class TestImageProcessing extends BaseDetectTest {
  boolean show = true;

  @Test
  public void testDenoiseByBlur() throws Exception {
    DenoiseByBlur dbb = new DenoiseByBlur(9,9);
    Mat frame = getTestImage();
    Mat blur = dbb.denoise(frame);
    assertEquals(blur.width(), frame.width());
    if (show) {
      ImageUtils.show(getTestImage());
      ImageUtils.show(blur); 
    }
  }

  @Test
  public void testColorFilter() throws Exception {
    ColorFilter cf=new ColorFilter();
    cf.setMinColorRGB( 65,  85,  85);
    cf.setMaxColorRGB(140, 140, 140);
    Mat frame = getTestImage();
    Mat gray=new Mat();
    Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
    assertEquals(2458261,Core.countNonZero(gray));
    Mat colorFiltered = cf.filter(frame);
    assertEquals(colorFiltered.width(), frame.width());
    Mat cfGray=new Mat();
    Imgproc.cvtColor(colorFiltered, cfGray, Imgproc.COLOR_BGR2GRAY);
    assertEquals(173768,Core.countNonZero(cfGray));
    if (show) {
      ImageUtils.show(getTestImage());
      ImageUtils.show(gray);
      ImageUtils.show(colorFiltered); 
    }
  }

}
