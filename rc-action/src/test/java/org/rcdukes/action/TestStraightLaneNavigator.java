package org.rcdukes.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.rcdukes.detect.ImageFetcher;
import org.rcdukes.detect.LaneDetector;
import org.rcdukes.geometry.LaneDetectionResult;
import org.rcdukes.opencv.NativeLibrary;
import org.rcdukes.video.Image;

import io.vertx.core.json.JsonObject;

/**
 * test the straight lane navigator
 * 
 * @author wf
 *
 */
public class TestStraightLaneNavigator {

  /**
   * get a LaneDetectionResult for the given Parameters
   * 
   * @param frameIndex
   * @param timeStamp
   * @param left
   * @param middle
   * @param right
   * @return
   */
  public LaneDetectionResult getLdr(int frameIndex, int timeStamp, Double left,
      Double middle, Double right) {
    LaneDetectionResult ldr = new LaneDetectionResult();
    ldr.frameIndex = frameIndex;
    ldr.milliTimeStamp = timeStamp;
    ldr.left = left;
    ldr.middle = middle;
    ldr.right = right;
    return ldr;
  }

  @Test
  public void testStraightLaneNavigator() {
    LaneDetectionResult ldrs[] = { getLdr(1, 0, -45., 0., 45.),
        getLdr(2, 100, -46., 1., 46.), getLdr(3, 200, -47., 0., 47.) };
    StraightLaneNavigator nav = new StraightLaneNavigator();
    for (LaneDetectionResult ldr : ldrs)
      nav.getNavigationInstruction(ldr);
    long nodeCount = nav.g().V().count().next().longValue();
    assertEquals(3, nodeCount);
  }

  @Test
  public void testFromJson() {
    Navigator navigator = new StraightLaneNavigator();
    JsonObject ldrJo = new JsonObject();
    LaneDetectionResult ldr = navigator.fromJsonObject(ldrJo);
    JsonObject nav = navigator.getNavigationInstruction(ldr);
    assertNull(nav);
  }

  @Test
  public void testFromVideo() throws Exception {
    NativeLibrary.load();
    Navigator nav = new StraightLaneNavigator();
    String testUrl = "http://wiki.bitplan.com/videos/full_run.mp4";
    ImageFetcher imageFetcher = new ImageFetcher(testUrl);
    imageFetcher.open();
    int frameIndex=0;
    while (frameIndex<100 && imageFetcher.hasNext() && !imageFetcher.isClosed()) {
      Image image = imageFetcher.fetch();
      frameIndex=image.getFrameIndex();
      System.out.println(frameIndex);
      if (imageFetcher.hasNext()) {
        LaneDetector laneDetector = LaneDetector.getDefault();
        LaneDetectionResult ldr = laneDetector.detect(image);
        JsonObject navJo = nav.getNavigationInstruction(ldr);
        if (navJo != null)
          System.out.println(navJo.encodePrettily());
      }
    }
    imageFetcher.close();
    long nodeCount = nav.g().V().count().next().longValue();
    assertEquals(100, nodeCount);
  }
}
