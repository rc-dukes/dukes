package org.rcdukes.action;

import static org.junit.Assert.*;

import org.junit.Test;
import org.rcdukes.geometry.LaneDetectionResult;

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
        getLdr(2, 100, -46., 1., 46.),getLdr(3,200,-47.,0.,47.) };
    StraightLaneNavigator nav = new StraightLaneNavigator();
    for (LaneDetectionResult ldr : ldrs)
      nav.getNavigationInstruction(ldr);
    long nodeCount = nav.g().V().count().next().longValue();
    assertEquals(3,nodeCount);
  }

}
