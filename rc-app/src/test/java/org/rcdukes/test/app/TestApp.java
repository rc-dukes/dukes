package org.rcdukes.test.app;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import javafx.geometry.Pos;

/**
 * test the JavaFX App
 * @author wf
 *
 */
public class TestApp {

  @Test
  public void testGeometry() {
    assertEquals(3,Pos.CENTER_LEFT.ordinal());
  }
  
}
