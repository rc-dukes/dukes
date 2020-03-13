package org.rcdukes.detect.lanedetection;

import static java.util.Optional.of;

import java.util.Optional;

import org.rcdukes.geometry.Lane;
import org.rcdukes.geometry.Line;
import org.rcdukes.geometry.Point;
import org.rcdukes.geometry.Point2D;
import org.rcdukes.objects.ViewPort;

/**
 * calculates the orientation of a given lane
 *
 */
public class LaneOrientation {

  private final Lane lane;
  private final ViewPort viewPort;
  private Line middle = null;
  private Line left = null;
  private Line right = null;

  /**
   * construct me for the given lane and viewport
   * 
   * @param lane
   * @param viewPort
   */
  public LaneOrientation(Lane lane, ViewPort viewPort) {
    this.lane = lane;
    this.viewPort = viewPort;
  }

  /**
   * determine the lines
   */
  public void determineLines() {
    setMiddle(determineLaneMiddle().orElse(null));
    setLeft(lane.getLeftBoundary().orElse(null));
    setRight(lane.getRightBoundary().orElse(null));
  }

  /**
   * get lines
   * 
   * @return - the three optional lines
   */
  public Line[] getLines() {
    Line lolines[] = { getLeft(), getMiddle(), getRight() };
    return lolines;
  }

  public double distanceFromLeftBoundary() {
    Optional<Point2D> viewPortMiddle = determineViewPortMiddle();
    return viewPortMiddle
        .flatMap(middle -> lane.getLeftBoundary()
            .map(boundary -> boundary.bottomMost().distance(middle)))
        .orElse(Double.NaN);
  }

  public double distanceFromRightBoundary() {
    Optional<Point2D> viewPortMiddle = determineViewPortMiddle();
    return viewPortMiddle
        .flatMap(middle -> lane.getRightBoundary()
            .map(boundary -> boundary.bottomMost().distance(middle)))
        .orElse(Double.NaN);
  }

  private Optional<Point2D> determineViewPortMiddle() {
    double positionFraction = determineFractionalPosition();

    Optional<Line> base = determineBase();
    return base.map(line -> line.pointAt(positionFraction));
  }

  private double determineFractionalPosition() {
    double distanceToMiddle = determineDistanceToMiddle();
    double fraction = distanceToMiddle / viewPort.getWidth();

    return 0.5 + fraction;
  }

  public double determineDistanceToMiddle() {
    Optional<Line> middle = determineLaneMiddle();
    return middle.map(line -> {
      Point2D middleBottom = line.bottomMost();

      double distanceToMiddle = -1 * middleBottom.distance(
          new Point(viewPort.getOrigin().getX() + (viewPort.getWidth() / 2),
              middleBottom.getY()));

      return distanceToMiddle;
    }).orElse(Double.NaN);
  }

  /**
   * determine the course relative to Horizon
   * 
   * @return
   */
  public Double determineCourseRelativeToHorizon() {
    if (middle == null) {
      return null;
    }
    double middleAtHorizonX = middle.topMost().getX();
    double middleHere=middle.bottomMost().getX();
    double maxX = viewPort.getWidth();
    // @TODO - distance to horizon is taken as 1 here ...
    double tan = (middleAtHorizonX-middleHere) / maxX;
    return Math.atan(tan);
  }

  public Optional<Line> determineLaneMiddle() {
    if (!lane.getLeftBoundary().isPresent()
        || !lane.getRightBoundary().isPresent()) {
      return Optional.empty();
    }
    Line left = lane.getLeftBoundary().get();
    Line right = lane.getRightBoundary().get();

    Optional<Line> extendedBase = determineBase();
    return extendedBase.map(base -> {
      Point2D infHorizon = left.intersect(right)
          .orElseThrow(() -> new RuntimeException(
              "Left and Right boundary do not intersect ..."));
      Point2D baseMiddle = base.pointAt(0.5);

      return new Line(baseMiddle, infHorizon);
    });
  }

  public Optional<Line> determineBase() {
    if (!lane.getLeftBoundary().isPresent()
        || !lane.getRightBoundary().isPresent()) {
      return Optional.empty();
    }

    Line left = lane.getLeftBoundary().get();
    Line right = lane.getRightBoundary().get();

    Point2D leftLow = left.bottomMost();
    Point2D leftLeft = left.leftMost();
    Point2D rightLow = right.bottomMost();
    Point2D rightRight = right.rightMost();

    Point2D lowest = leftLow.getY() > rightLow.getY() ? leftLow : rightLow;

    Line base = new Line(new Point(leftLeft.getX(), lowest.getY()),
        new Point(rightRight.getX(), lowest.getY()));

    Point2D leftIntersect = base.intersect(left).orElseThrow(
        () -> new RuntimeException("Left does not intersect base ..."));
    Point2D rightIntersect = base.intersect(right).orElseThrow(
        () -> new RuntimeException("Right does not intersect base ..."));

    return of(new Line(leftIntersect, rightIntersect));
  }

  /**
   * @return the middle
   */
  public Line getMiddle() {
    return middle;
  }

  /**
   * @param middle
   *          the middle to set
   */
  public void setMiddle(Line middle) {
    this.middle = middle;
  }

  /**
   * @return the left
   */
  public Line getLeft() {
    return left;
  }

  /**
   * @param left
   *          the left to set
   */
  public void setLeft(Line left) {
    this.left = left;
  }

  /**
   * @return the right
   */
  public Line getRight() {
    return right;
  }

  /**
   * @param right
   *          the right to set
   */
  public void setRight(Line right) {
    this.right = right;
  }
}