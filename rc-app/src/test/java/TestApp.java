import static org.junit.Assert.*;

import org.junit.Test;

import javafx.geometry.Pos;

public class TestApp {

  @Test
  public void testGeometry() {
    assertEquals(3,Pos.CENTER_LEFT.ordinal());
  }

}
