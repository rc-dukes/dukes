package org.rcdukes.detect;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.rcdukes.detect.linedetection.HoughLinesLineDetector;
import org.rcdukes.geometry.Line;
import org.rcdukes.video.ImageUtils;
import org.rcdukes.video.ImageUtils.CVColor;

/**
 * test for hough lines detection
 * 
 * @author wf
 *
 */
public class TestHoughLinesDetector extends BaseDetectTest {

  public Mat getCanny(Mat frame, double treshold1, double treshold2,
      int apertureSize) {
    Mat mGray = new Mat();
    Imgproc.cvtColor(frame, mGray, Imgproc.COLOR_BGR2GRAY);
    Mat canny = new Mat();
    Imgproc.Canny(mGray, canny, treshold1, treshold2, apertureSize, false);
    return canny;
  }

  public Mat getTestImage2() throws Exception {
    Mat frame = ImageUtils.fromResource(this.getClass(), "images/sudoku.jpg");
    return frame;
  }

  @Test
  public void testProbabilisticHoughLinesDetector() throws Exception {
    HoughLinesLineDetector hld = new HoughLinesLineDetector();
    Mat frame = getTestImage2();
    Mat canny = getCanny(frame, 50, 200, 3); // 772,65
    Collection<Line> lines = hld.detect(canny);
    assertEquals(192, lines.size());
    ImageUtils iu = new ImageUtils();
    iu.writeImageWithLines(frame, lines, "houghlinesp.jpg", CVColor.red);
  }

  @Test
  public void testHoughLinesDetector() throws Exception {
    HoughLinesLineDetector hld = new HoughLinesLineDetector();
    hld.setProbabilistic(false);
    hld.setThreshold(150);
    Mat frame = getTestImage2();
    Mat canny = getCanny(frame, 50,200,3);
    Collection<Line> lines = hld.detect(canny);
    assertEquals(62, lines.size());
    ImageUtils iu = new ImageUtils();
    iu.writeImageWithLines(frame, lines, "houghlines.jpg", CVColor.dodgerblue);
  }

}
