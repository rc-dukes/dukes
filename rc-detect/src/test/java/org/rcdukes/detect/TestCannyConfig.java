package org.rcdukes.detect;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.rcdukes.detect.edgedectection.CannyEdgeDetector;

import io.vertx.core.json.JsonObject;

public class TestCannyConfig {

  @Test
  public void testCannyConfig() {
     String json="{\n" + 
         "  \"threshold1\" : 87,\n" + 
         "  \"threshold2\" : 150\n" + 
         "}";
     JsonObject jo=new JsonObject(json); 
     CannyEdgeDetector detector = jo.mapTo(CannyEdgeDetector.class);
     assertEquals(87,detector.getThreshold1(),0.1);
     assertEquals(150,detector.getThreshold2(),0.1);
     
  }
}
