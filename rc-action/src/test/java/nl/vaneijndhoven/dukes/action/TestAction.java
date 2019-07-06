package nl.vaneijndhoven.dukes.action;

import static nl.vaneijndhoven.dukes.action.drag.StraightLaneNavigator.COMMAND_LOOP_INTERVAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import io.vertx.core.json.JsonObject;
import nl.vaneijndhoven.dukes.action.drag.StraightLaneNavigator;
import rx.Observable;
import rx.observers.TestSubscriber;

/**
 * test Action/Luke
 *
 */
public class TestAction {
  public static boolean debug = true;
  public static long TIME_OUT = COMMAND_LOOP_INTERVAL * 2; // we expect a result
                                                           // within 50 msecs

  /**
   * check the observed value https://stackoverflow.com/a/31330706/1497139
   * 
   * @param obs
   *          - the observable to check
   * @param timeOutMilliSecs
   *          - the number of milliseconds until the observable will timeout
   * @param nameValues
   *          - an array of name - value object pairs
   */
  public void check(Observable<JsonObject> obs, long timeOutMilliSecs,
      String... nameValues) {
    if (nameValues.length % 2 != 0)
      throw new IllegalArgumentException(
          "nameValue parameter list length may not be odd");
    TestSubscriber<JsonObject> subscriber = TestSubscriber.create();

    obs.subscribe(subscriber);

    assertTrue("timed out waiting for on next event",
        subscriber.awaitValueCount(1, timeOutMilliSecs, TimeUnit.MILLISECONDS));
    subscriber.assertCompleted();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    JsonObject nav = subscriber.getOnNextEvents().get(0);
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
    if (!TestSuite.isTravis()) {
      StraightLaneNavigator navigator = new StraightLaneNavigator();

      // Sleep (after creating the navigator) to ensure we surpass the command
      // loop interval, otherwise no
      // navigation command will be issued.
      Thread.sleep(COMMAND_LOOP_INTERVAL);
      TestSubscriber<JsonObject> subscriber = TestSubscriber.create();
      JsonObject laneDetectResult = new JsonObject();
      Observable<JsonObject> nav = navigator.navigate(laneDetectResult);
      nav.subscribe(subscriber);

      subscriber.assertCompleted();
      subscriber.assertNoErrors();
      subscriber.assertValueCount(0);
    }
  }

  @Test
  public void testStraightLaneNavigator2() throws InterruptedException {
    if (!TestSuite.isTravis()) {
      StraightLaneNavigator navigator = new StraightLaneNavigator();

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
        JsonObject laneDetectResult = new JsonObject();
        laneDetectResult.put("angle", angle);
        Observable<JsonObject> nav2 = navigator.navigate(laneDetectResult);
        check(nav2, TIME_OUT, "type", "servoDirect", "position",
            expected[index]);
        index++;
      }
    }
  }

}
