package nl.vandeindhoven.dukes.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import nl.vaneijndhoven.dukes.geometry.Line;
import nl.vaneijndhoven.dukes.geometry.Point;
import nl.vaneijndhoven.dukes.geometry.Point2D;
import nl.vaneijndhoven.dukes.geometry.Point3D;
import nl.vaneijndhoven.dukes.geometry.Polygon;
import nl.vaneijndhoven.dukes.geometry.Line.Vector;

/**
 * test the Geometry functions
 * 
 * @author wf
 *
 */
public class TestGeometry {

  @Test
  public void testLine() {
    Line line = new Line(0., 0., 100., 100.);
    Line liner = new Line(100., 100., 0.0, 0.0);
    Line line2 = new Line(0., 10., 100, 110.);
    Line line3 = new Line(50.0, 0, 50.0, 100.);
    assertEquals(141.0, line.length(), 0.5);
    assertEquals(Math.PI / 4, line.angleRad(), 0.01);
    assertEquals(45.0, line.angleDeg(), 0.01);
    assertEquals(0., line.getPoint1().getX(), 0.01);
    assertEquals(0., line.getPoint1().getY(), 0.01);
    assertEquals(100., line.getPoint2().getX(), 0.01);
    assertEquals(100., line.getPoint2().getY(), 0.01);
    Line avgLine = Line.average(line, line2);
    assertEquals("{0.0,5.0} - {100.0,105.0}", avgLine.toString());
    Point2D pRight = new Point(50.0, 40.0);
    Point2D pLeft = new Point(50.0, 60.0);
    Point2D pOn = new Point(50.0, 50.0);
    assertTrue(line.isLeftOfLine(pLeft));
    assertFalse(line.isRightOfLine(pLeft));
    assertFalse(line.existsOnLine(pLeft));
    assertFalse(line.isLeftOfLine(pRight));
    assertTrue(line.isRightOfLine(pRight));
    assertFalse(line.existsOnLine(pRight));
    assertFalse(line.isLeftOfLine(pOn));
    assertFalse(line.isRightOfLine(pOn));
    assertTrue(line.existsOnLine(pOn));
    // coverage of existsOnLine
    assertFalse(line.existsOnLine(new Point(50.0, 120.0)));
    assertFalse(line.existsOnLine(new Point(50.0, -20.0)));

    assertEquals("{100.0,100.0}", line.bottomMost().toString());
    assertEquals("{100.0,100.0}", line.rightMost().toString());
    assertEquals("{0.0,0.0}", line.topMost().toString());
    assertEquals("{0.0,0.0}", line.leftMost().toString());
    assertEquals("{100.0,100.0}", liner.bottomMost().toString());
    assertEquals("{100.0,100.0}", liner.rightMost().toString());
    assertEquals("{0.0,0.0}", liner.topMost().toString());
    assertEquals("{0.0,0.0}", liner.leftMost().toString());
    assertEquals("{50.0,50.0}", line.pointAt(0.5).toString());
    assertEquals("{50.0,50.0}", line.intersect(line3).get().toString());
    assertFalse(line.intersect(line2).isPresent());
    assertEquals(7.071, line.distance(pLeft), 0.0001);
  }

  @Test
  public void testVector() {
    assertEquals("{1.000000,0.000000}", Vector.forX(1).toString());
    assertEquals("{0.000000,2.000000}", Vector.forY(2).toString());
    Vector vector = new Vector(0.5, 0.5);
    assertEquals(2.0, vector.calculateX(2), 0.01);
    assertEquals(4.0, vector.calculateY(4), 0.01);
  }

  @Test
  public void testPoint() {
    try {
      new Point();
      fail("exception expected");
    } catch (IllegalArgumentException e) {
      assertEquals("Point must have at least 1 dimension", e.getMessage());
    }
    Point p = new Point(0, 0);
    try {
      p.distance();
      fail("exception expected");
    } catch (IllegalArgumentException e) {
      assertEquals(
          "Can only calculate distance between two points with similar dimensions.",
          e.getMessage());
    }
    Point3D p3 = new Point(0, 0, 0);
    assertEquals(0.0, p3.getZ(), 0.001);
    assertEquals(Math.sqrt(3), p3.distance(new Point(1, 1, 1)), 0.00001);
  }

  @Test
  public void testPolygon() {
    Polygon squares[] = { Polygon.square(new Point(0, 0), new Point(100, 100)),
        new Polygon(new Point(0, 0), new Point(100, 100), new Point(100, 0),
            new Point(0, 100)) };
    double expectedShoelace[] = { 20000, 0 };
    int index = 0;
    Line line = new Line(0, 25, 100, 25);
    for (Polygon square : squares) {
      List<Point2D> points = square.getPoints();
      List<Line> edges = square.edges();
      List<Point2D> cpoints = square.getPointsClockwise();
      List<Point2D> ccpoints = square.getPointsCounterClockwise();
      assertEquals(4, points.size());
      assertEquals(4, edges.size());
      assertEquals(4, cpoints.size());
      assertEquals(4, ccpoints.size());
      for (int i = 0; i < points.size(); i++) {
        assertEquals(points.get(i), cpoints.get(i));
        assertEquals(points.get(i), ccpoints.get(3 - i));
      }
      assertEquals(expectedShoelace[index++], square.shoelace(), 0.01);
      System.out.println(square.intersect(line));
    }

  }

}
