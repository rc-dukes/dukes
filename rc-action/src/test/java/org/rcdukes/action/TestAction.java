package org.rcdukes.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.rcdukes.action.StraightLaneNavigator.COMMAND_LOOP_INTERVAL;

import org.junit.BeforeClass;
import org.junit.Test;
import org.rcdukes.common.ClusterStarter;
import org.rcdukes.common.DukesVerticle.Status;
import org.rcdukes.common.Environment;
import org.rcdukes.geometry.LaneDetectionResult;

import io.vertx.core.json.JsonObject;

/**
 * test Action/Luke
 *
 */
public class TestAction {
  public static boolean debug = true;
  public static long TIME_OUT = COMMAND_LOOP_INTERVAL * 3; // we expect a result
  // within 50 msecs

  @BeforeClass
  public static void setTiming() {
    // in Travis environment things might be *really* slow
    if (TestSuite.isTravis()) {
      // increase the timing
      int TRAVIS_FACTOR = 2000; // 50 worked - 25 and 37 did not -
      // 60/75 failed in one instance and even 300/600 !
      COMMAND_LOOP_INTERVAL = COMMAND_LOOP_INTERVAL * TRAVIS_FACTOR;
      TIME_OUT = TIME_OUT * TRAVIS_FACTOR;
    }
  }

  /**
   * check the navigation result
   * 
   * @param nav
   *          - the navigation instrucion
   * @param nameValues
   *          - an array of name - value object pairs
   */
  public void check(JsonObject nav, String... nameValues) {
    if (nameValues.length % 2 != 0)
      throw new IllegalArgumentException(
          "nameValue parameter list length may not be odd");
    if (debug) {
      for (String key : nav.fieldNames()) {
        System.out.println(String.format("%s=%s", key, nav.getValue(key)));
      }
    }
    for (int i = 0; i < nameValues.length; i += 2) {
      String name = nameValues[i].toString();
      String value = nameValues[i + 1].toString();
      String foundValue = nav.getValue(name).toString();
      assertEquals(value, foundValue);
    }
  }

  @Test
  public void testStraightLaneNavigator() throws InterruptedException {
    Navigator navigator = new StraightLaneNavigator("+");
    JsonObject ldrJo = new JsonObject();
    LaneDetectionResult ldr = navigator.fromJsonObject(ldrJo);
    JsonObject nav = navigator.getNavigationInstruction(ldr);
    assertNull(nav);
  }

  @Test
  public void testStraightLaneNavigator2() throws InterruptedException {
    Navigator navigator = new StraightLaneNavigator("+");

    // Sleep (after creating the navigator) to ensure we surpass the command
    // loop interval, otherwise no
    // navigation command will be issued.
    Thread.sleep(COMMAND_LOOP_INTERVAL);

    double angles[] = { -45.0, -30.0, -15.0, -10.0, 0.0, 10.0, 15.0, 30.0,
        45.0 };
    String expected[] = { "-100.0", "-100.0", "-60.0", "-40.0", "0.0", "60.0",
        "90.0", "100.0", "100.0" };
    int index = 0;
    for (double angle : angles) {
      Thread.sleep(COMMAND_LOOP_INTERVAL);
      JsonObject ldrJo = new JsonObject();
      ldrJo.put("angle", angle);
      LaneDetectionResult ldr = navigator.fromJsonObject(ldrJo);
      JsonObject nav2 = navigator.getNavigationInstruction(ldr);
      check(nav2, "type", "servoDirect", "position", expected[index]);
      index++;
    }
  }


  @Test
  public void testAction() throws Exception {
    int TIME_OUT = 20000;
    Environment.mock();
    ClusterStarter clusterStarter = new ClusterStarter();
    Action action = new Action();
    clusterStarter.deployVerticles(action);
    action.waitStatus(Status.started, TIME_OUT, 10);
    if (!TestSuite.isTravis()) {
      clusterStarter.undeployVerticle(action);
      action.waitStatus(Status.stopped, TIME_OUT, 10);
    }
  }

}
