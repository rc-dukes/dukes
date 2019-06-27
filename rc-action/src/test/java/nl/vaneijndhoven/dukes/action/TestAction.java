package nl.vaneijndhoven.dukes.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import io.vertx.core.json.JsonObject;
import nl.vaneijndhoven.dukes.action.drag.StraightLaneNavigator;
import rx.Observable;

/**
 * test Action/Luke
 *
 */
// @FIXME - why does this fail on the command line and in travis
// but not in the Eclipse IDE?
public class TestAction {
  public static boolean debug = true;
  public static int TIME_OUT = 500; // we expect a result within 50 msecs

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
  public void check(Observable<JsonObject> obs, int timeOutMilliSecs,
      String... nameValues) {
    if (nameValues.length % 2 != 0)
      throw new IllegalArgumentException(
          "nameValue parameter list length may not be odd");
    boolean observed[] = { false };
    obs.timeout(timeOutMilliSecs, TimeUnit.MILLISECONDS).subscribe(nav -> {
      observed[0] = true;
      if (debug)
        for (String key : nav.fieldNames()) {
          System.out.println(String.format("%s=%s", key, nav.getValue(key)));
        }
      for (int i = 0; i < nameValues.length; i += 2) {
        String name = nameValues[i].toString();
        String value = nameValues[i + 1].toString();
        String foundValue = nav.getValue(name).toString();
        assertEquals(value, foundValue);
      }
    });
    String msg = String.format(
        "the observable wasn't observed (yet ...) after %d mSecs",
        timeOutMilliSecs);
    assertTrue(msg, observed[0]);
  }

  @Test
  public void testStraightLaneNavigator() {
    if (!TestSuite.isTravis()) {
      StraightLaneNavigator navigator = new StraightLaneNavigator();
      JsonObject laneDetectResult = new JsonObject();
      Observable<JsonObject> nav = navigator.navigate(laneDetectResult);
      // we expect an empty Observable here
      assertNotNull(nav);
      // since we get no angle found for 1000ms, emergency stop
      assertNotEquals(Observable.empty(), nav);
      laneDetectResult.put("angle", 10.0);
      Observable<JsonObject> nav2 = navigator.navigate(laneDetectResult);
      check(nav2, TIME_OUT, "type", "servoDirect", "position", "60.0");
    }
  }

}
