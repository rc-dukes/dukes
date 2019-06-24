package nl.vandeindhoven.dukes.geometry;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import nl.vaneijndhoven.dukes.geometry.Line;

/**
 * test the Geometry functions
 * @author wf
 *
 */
public class TestGeometry {

  @Test
  public void testLine() {
    Line line=new Line(0.,0.,100.,100.);
    Line line2=new Line(0.,10.,100,110.);
    assertEquals(141.0,line.length(),0.5);
    assertEquals(Math.PI/4,line.angleRad(),0.01);
    assertEquals(45.0,line.angleDeg(),0.01);
    assertEquals(0.,line.getPoint1().getX(),0.01);
    assertEquals(0.,line.getPoint1().getY(),0.01);
    assertEquals(100.,line.getPoint2().getX(),0.01);
    assertEquals(100.,line.getPoint2().getY(),0.01);
    Line avgLine = Line.average(line,line2);
    assertEquals("{0.0,5.0} - {100.0,105.0}",avgLine.toString());
  }

}
