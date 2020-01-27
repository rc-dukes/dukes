package org.rcdukes.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.rcdukes.common.ServoCalibration;

import com.google.gson.Gson;

import io.vertx.core.json.JsonObject;

/**
 * test Plain Old Java Object conversion with vert.x
 * @author wf
 *
 */
public class TestPOJO {
  boolean debug=true;
  @Test
  /**
   * test Vert.x pojo support
   * see <a href='http://vertx.io/docs/apidocs/io/vertx/core/json/JsonObject.html>JsonObject</a>
   */
  public void testPOJO() {
    // https://stackoverflow.com/a/42003953/1497139
    ServoCalibration sc = new ServoCalibration();
    sc.setVehicle("Ford");
    sc.setType("engine");
    sc.setUnit("m/s");
    sc.getValueMap().put(141, 1.19);
    sc.getValueMap().put(142, 1.24);
    sc.getValueMap().put(143, 1.47);
    sc.getValueMap().put(144, 1.8);
    sc.getValueMap().put(145, 2.0);
    sc.getValueMap().put(146, 2.2);
    sc.getValueMap().put(147, 2.2);    
    
    Gson gson = new Gson();
    JsonObject jo = sc.asJsonObject();
    String vjson=jo.toString();
    String gjson=gson.toJson(sc);
    if (debug) {
      System.out.println(vjson);
      System.out.println(sc);
    }
    assertEquals(vjson,gjson);
    ServoCalibration sc2 = ServoCalibration.fromJo(jo);
    assertEquals(sc.asJson(),sc2.asJson());
  }
}
