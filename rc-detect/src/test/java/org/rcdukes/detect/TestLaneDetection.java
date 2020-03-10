package org.rcdukes.detect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static java.util.Optional.ofNullable;
import java.util.Optional;

import org.junit.Test;
import org.opencv.core.Mat;
import org.rcdukes.detect.lanedetection.LaneOrientation;
import org.rcdukes.geometry.Lane;
import org.rcdukes.geometry.LaneDetectionResult;
import org.rcdukes.geometry.Line;
import org.rcdukes.geometry.Point;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageCollector;
import org.rcdukes.video.ImageCollector.ImageType;
import org.rcdukes.video.ImageUtils;

import io.vertx.core.json.JsonObject;
import nl.vaneijndhoven.objects.ViewPort;
import nl.vaneijndhoven.opencv.edgedectection.CannyEdgeDetector;

/**
 * test the lane detection
 * 
 * @author wf
 *
 */
public class TestLaneDetection extends BaseDetectTest {

  /**
   * detect based on the given image
   * 
   * @param image
   * @param prefix
   */
  public LaneDetectionResult detect(Image image, String prefix,
      double threshold1, double threshold2) {
    LaneDetector ld = LaneDetector.getDefault();
    CannyEdgeDetector ced = (CannyEdgeDetector) ld.getEdgeDetector();
    ced.setThreshold1(threshold1);
    ced.setThreshold2(threshold2);
    LaneDetectionResult ldr = ld.detect(image);
    ImageCollector c = ld.getCollector();
    assertNotNull(c);
    ImageUtils imageUtils = new ImageUtils();
    for (ImageType imageType : ImageType.values()) {
      Image cimage = c.getImage(imageType, true);
      if (cimage.getFrame() != null)
        imageUtils.writeImage(cimage.getFrame(),
            prefix + cimage.getName() + Image.ext);
    }
    return ldr;
  }

  @Test
  public void testLaneDetection() throws Exception {
    ImageUtils imageUtils = new ImageUtils();
    Mat frame = super.getTestImage();
    assertNotNull(frame);
    assertEquals(1920, frame.width());
    assertEquals(1281, frame.height());
    for (int t1 = 304; t1 <= 304; t1++) {
      for (int t2 = 360; t2 <= 360; t2++) {
        imageUtils.writeImage(frame, "road" + t1 + "_" + t2 + ".jpg");
        Image image = new Image(frame, "road" + t1 + "_" + t2, 0,
            System.currentTimeMillis());
        LaneDetectionResult ldr = detect(image, image.getName(), t1, t2);
        assertNotNull(ldr);
        if (debug) {
          System.out.println(ldr.debugInfo());
        }
      }
    }
  }

  @Test
  public void testLaneDetectionWithImageFetcher() {
    ImageFetcher imageFetcher = TestImageFetcher.getTestImageFetcher();
    imageFetcher.open();
    Image image = imageFetcher.fetch();
    Mat frame = image.getFrame();
    assertEquals(1, image.getFrameIndex());
    assertEquals(768, frame.width());
    assertEquals(576, frame.height());
    assertEquals(3, frame.channels());
    detect(image, "Utrecht", 64, 50);
    imageFetcher.close();
  }

  /**
   * get a test image for the lane detection
   * 
   * @return the test image
   * @throws Exception
   */
  public Image getLaneDetectionTestImage() throws Exception {
    Mat frame = super.getTestImage();
    Image image = new Image(frame, "road", 0, System.currentTimeMillis());
    return image;
  }

  /**
   * test the lane orientation
   */
  @Test
  public void testLaneOrientation() {
    Line left = new Line(-422.5, 537.0, 1278.0, -0.0);
    Line middle = new Line(576.25, 537.0, 693.75, 184.5);
    Line right = new Line(1575.0, 537.0, 232.5, -0.0);
    Line[] lines = { left, middle, right };
    double angles[] = { -17.52, -71.56, -158.19 };
    int index = 0;
    for (Line line : lines) {
      String msg = String.format("expected %.2f° for index %d", angles[index],
          index);
      assertEquals(msg, angles[index++], line.angleDeg(), 0.01);
    }
    Lane lane = new Lane(ofNullable(left), ofNullable(right));
    ViewPort viewPort = new ViewPort(new Point(0.0, 0.0), 1280, 537);
    LaneOrientation lo = new LaneOrientation(lane, viewPort);
    for (Line line : lo.getLines()) {
      assertNull(line);
    }
    lo.determineLines();
    index = 0;
    for (Line line : lo.getLines()) {
      assertNotNull(line);
      String msg = String.format("expected %.2f° for index %d", angles[index],
          index);
      assertEquals(msg, angles[index++], line.angleDeg(), 0.01);
    }
    Double cth = lo.determineCourseRelativeToHorizon();
    assertNotNull(cth);
    if (debug)
      System.out.println(Math.toDegrees(cth));
  }

  @Test
  public void testOptional() {
    String[] tests = { null, "test" };
    boolean expected[] = { false, true };
    int index = 0;
    for (String test : tests) {
      Optional<String> s = ofNullable(test);
      assertTrue("" + index, expected[index++] == s.isPresent());
    }
  }

  @Test
  public void testJackson() {
    Point point = new Point(0.0, 12.5);
    JsonObject pJo = JsonObject.mapFrom(point);
    String json = pJo.encodePrettily();
    if (debug)
      System.out.println(json);
    Point point2 = pJo.mapTo(Point.class);
    assertEquals(point.getX(), point2.getX(), 0.00001);
    assertEquals(point.getY(), point2.getY(), 0.00001);
  }

  @Test
  public void testDetermineCurrentAngle() throws Exception {
    Image testImage = this.getLaneDetectionTestImage();
    LaneDetectionResult ldr = detect(testImage, testImage.getName(), 304, 360);
    assertNotNull(ldr.middle);
    double angle = ldr.middle.angleDeg() + 90;
    double expectedAngle = -7.55;
    double expectedCourse = -2.48;
    if (TestSuite.isTravis()) {
      expectedAngle = -7.55;
      expectedCourse = 20.37;
    } else {
      assertEquals(expectedAngle, angle, 0.01);
      assertEquals(expectedCourse, Math.toDegrees(ldr.courseRelativeToHorizon),
          0.01);
    }
    if (debug)
      System.out.println(ldr.debugInfo());
    JsonObject ldrJo = JsonObject.mapFrom(ldr);
    String json = ldrJo.encodePrettily();
    if (debug)
      System.out.println(json);
    LaneDetectionResult ldr2 = ldrJo.mapTo(LaneDetectionResult.class);
    if (debug)
      System.out.println(ldr2.debugInfo());
  }
  
  @Test
  public void testDebugInfo() {
    LaneDetectionResult ldr=new LaneDetectionResult();
    String info=ldr.debugInfo();
    assertEquals("  left: ?\nmiddle: ?\n right: ?\ncourse: ?",info);
  }

}
