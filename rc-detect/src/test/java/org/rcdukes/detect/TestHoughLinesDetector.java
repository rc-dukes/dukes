package org.rcdukes.detect;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.opencv.core.Mat;
import org.rcdukes.detect.linedetection.HoughLinesLineDetector;
import org.rcdukes.geometry.Line;
import org.rcdukes.video.ImageUtils;

/**
 * test for hough lines detection
 * @author wf
 *
 */
public class TestHoughLinesDetector extends BaseDetectTest {

  @Test
  public void testHoughLinesDetector() throws Exception {
    HoughLinesLineDetector hld = new HoughLinesLineDetector();
    Mat frame=ImageUtils.fromResource(this.getClass(), "images/edges_2020-01-29151752.jpg");
    Collection<Line> lines = hld.detect(frame);
    assertEquals(2257,lines.size());
  }

}
