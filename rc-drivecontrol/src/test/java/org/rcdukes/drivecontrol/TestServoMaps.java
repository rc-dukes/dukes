package org.rcdukes.drivecontrol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.rcdukes.car.ServoRange;
import org.rcdukes.car.ServoSide;
import org.rcdukes.common.Environment;
import org.rcdukes.common.ServoPosition;

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
  
  @Test
  public void testAngles() {
    double angles[]= {-45,-30,-20,-10,-5,0,5,10,20,30,45};
    double expectedValues[] = {-20,-20,-20,-10,-5,0,5,10,20,25,25};
    SteeringMap smap = new SteeringMap(sc);
    int index=0;
    for (double angle:angles) {
      ServoPosition anglePos = smap.atValue(angle);
      String msg=String.format("steering %5.1f° (%3d) for wanted angle %5.1f°", anglePos.getValue(),anglePos.getServoPos(),angle);
      if (debug)
        System.out.println(msg);
      assertEquals(expectedValues[index],anglePos.getValue(),0.001);
      index++;
    }
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
    map.newPosition(map.getRange().getMin());
    int maxPos=map.getRange().getMax().getServoPos();
    while (map.getCurrentPosition().getServoPos() < maxPos) {
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
  
  public void check(int servoPos,ServoRange range,ServoSide side, boolean expected) {
    int clampedServoPos=range.clampServoPos(servoPos);
    boolean in=side.isServoPosOnSide(clampedServoPos);
    String msg=String.format("servopos %d clamped to %d should be on side %s within %d and %d",servoPos,clampedServoPos,side.getName(),side.getMin().getServoPos(),side.getMax().getServoPos());
    assertTrue(msg,in==expected);
  }
  
  @Test
  public void testIsOnSide() {
    ServoPosition minLeft=new ServoPosition(130,-20);
    ServoPosition maxLeft=new ServoPosition(100,-10);
    ServoSide left=new ServoSide("left", -1.0, minLeft,maxLeft);
    ServoPosition minRight=new ServoPosition(140,10);
    ServoPosition maxRight=new ServoPosition(160,20);
    ServoSide right=new ServoSide("right", 1.0, minRight,maxRight);
    ServoPosition center=new ServoPosition(135,0);
    ServoRange range=new ServoRange(1, left,center, right);
    
    check(115,range,left,true);
    check(90,range,left,true);
    check(135,range,left,false);
    check(150,range,right,true);
    check(180,range,right,true);
    check(135,range,right,false);
  }

}
