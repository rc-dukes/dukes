package org.rcdukes.drivecontrol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.rcdukes.car.ServoPosition;
import org.rcdukes.car.ServoSide;
import org.rcdukes.common.Environment;

/**
 * test EngineMap
 * 
 * @author wf
 *
 */
public class TestServoMaps {
  private static ServoCommandDummy sc;
  public static boolean debug = true;

  @BeforeClass
  public static void prepare() {
    Environment.mock();
    sc = new ServoCommandDummy();
  }

  @Test
  public void testEngineMap() {
    EngineMap emap = new EngineMap(sc);
    int percents[] = { -200, -100, -50, -25, 0, 25, 50, 100, 200 };
    int expectedServoPos[] = { 120, 120, 122, 122, 130, 140, 143, 147, 147,
        120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147};
    double expectedValues[] = { -2.2, -2.2, -1.7, -1.4, 0.0, 1.4, 1.7, 2.2,
        2.2,
        -2.2,-1.9,-1.5,-1.2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1.2,1.3,1.4,1.5,1.6,1.8,1.9,2.0,2.1,2.2};
    checkServoRangeMap(emap, percents, expectedServoPos, expectedValues);
  }

  @Test
  public void testCheck() {
    EngineMap emap = new EngineMap(sc);
    emap.getRange().setStepSize(0);
    try {
      emap.check();
      fail("There should be an exception");
    } catch (Throwable e) {
      System.err.println(e.getMessage());
    }
  }

  @Test
  public void testSteeringMap() {
    SteeringMap smap = new SteeringMap(sc);
    int percents[] = { -200, -100, -50, -25, 0, 25, 50, 100, 200 };
    int expectedServoPos[] = { 100, 100, 115, 123, 130, 138, 145, 160, 160,
        100,105,110,115,120,125,130,135,140,145,150,155,160};
    double expectedValues[] = { -20, -20, -10, -5, 0, 6.3, 12.5, 25, 25, 
        -20,-16.7,-13.3,-10.0,-6.7,-3.3,0,4.2,8.3,12.5,16.7,20.8,25.0};
    checkServoRangeMap(smap, percents, expectedServoPos, expectedValues);
  }

  private void checkServoRangeMap(ServoRangeMap map, int[] percents,
      int[] expectedServoPos, double[] expectedValues) {
    ServoPosition cpos = map.getCurrentPosition();
    assertEquals(0, cpos.getValue(), 0.0001);
    int index = 0;
    for (int percent : percents) {
      map.newPosition(map.atPercent(percent));
      cpos = map.getCurrentPosition();
      if (debug)
        System.out.println(map.positionInfo());
      String hint=String.format("expected %.1f%s (%3d) at index %3d",expectedValues[index],map.getUnit(),expectedServoPos[index],index);
      assertEquals(hint,expectedServoPos[index], cpos.getServoPos());
      assertEquals(hint,expectedValues[index], cpos.getValue(), 0.05);
      index++;
    }
    map.newPosition(map.getRange().getSideN().getMin());
    while (map.getCurrentPosition().getServoPos() <= map.getRange().getSideP()
        .getMax().getServoPos()) {
      if (debug)
        System.out.println(map.positionInfo());
      cpos = map.getCurrentPosition();
      String hint=String.format("expected %.1f%s (%3d) at index %3d",expectedValues[index],map.getUnit(),expectedServoPos[index],index);
      assertEquals(hint,expectedServoPos[index], cpos.getServoPos());
      assertEquals(hint,expectedValues[index], cpos.getValue(), 0.05);
      index++;
      map.step(1);
    }
  }
  
  @Test
  public void testIsOnSide() {
    ServoPosition minLeft=new ServoPosition(150,-20);
    ServoPosition maxLeft=new ServoPosition(100,-10);
    ServoSide left=new ServoSide("left", -1.0, minLeft,maxLeft);
    assertTrue("125 on left",left.isOnSide(125, false));
    assertTrue("175 on left",left.isOnSide(175, false));
    assertFalse("95 middle",left.isOnSide(95, false));
    ServoPosition minRight=new ServoPosition(90,10);
    ServoPosition maxRight=new ServoPosition(50,20);
    ServoSide right=new ServoSide("right", 1.0, minRight,maxRight);
    assertTrue("70 on right",right.isOnSide(70, false));
    assertTrue("30 on right",right.isOnSide(30, false));
    assertFalse("95 middle",right.isOnSide(95, false));
  }

}
