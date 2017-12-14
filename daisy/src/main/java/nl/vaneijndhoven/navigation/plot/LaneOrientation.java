package nl.vaneijndhoven.navigation.plot;

import nl.vaneijndhoven.geometry.*;
import nl.vaneijndhoven.objects.Lane;
import nl.vaneijndhoven.objects.ViewPort;

import java.util.Optional;

import static java.util.Optional.of;

public class LaneOrientation {

    private final Lane lane;
    private final ViewPort viewPort;

    public LaneOrientation(Lane lane, ViewPort viewPort) {
        this.lane = lane;
        this.viewPort = viewPort;
    }
    //   \
    //    \
    //     \
    //      \
    public double determineCurrentAngle() {
        Optional<Line> middle = determineLaneMiddle();
        Optional<Line> left = lane.getLeftBoundary();
        Optional<Line> right = lane.getRightBoundary();


        double angDeg = 0;
        // if left and right are visible, use the line that's longest
        if (middle.isPresent()) {
            angDeg = middle.get().angleDeg();
            return angDeg + 90;
        } else
        if (left.isPresent() && right.isPresent() && left.get().length() > 0 && right.get().length() > 0) {
            if (left.get().length() < right.get().length()) {
                 angDeg = left.get().angleDeg();
            } else {
                 angDeg = right.get().angleDeg();
            }
        // if only left visible, use left
        } else if (left.isPresent() && left.get().length() > 0) {
                angDeg = left.get().angleDeg();
        // if only right visible, use left
        } else if (right.isPresent() && right.get().length() > 0) {
                angDeg = right.get().angleDeg();
        }



        if (angDeg == 0) {
            return Double.NaN;
        }

        return -1 * angDeg - 90;
    }

    public double distanceFromLeftBoundary() {
        Optional<Point2D> viewPortMiddle = determineViewPortMiddle();
        return viewPortMiddle.flatMap(middle -> lane.getLeftBoundary().map(boundary -> boundary.bottomMost().distance(middle))).orElse(Double.NaN);
    }

    public double distanceFromRightBoundary() {
        Optional<Point2D> viewPortMiddle = determineViewPortMiddle();
        return viewPortMiddle.flatMap(middle -> lane.getRightBoundary().map(boundary -> boundary.bottomMost().distance(middle))).orElse(Double.NaN);
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

            double distanceToMiddle = -1 * middleBottom.distance(new Point(viewPort.getOrigin().getX() + (viewPort.getWidth() / 2), middleBottom.getY()));

            return distanceToMiddle;
        }).orElse(Double.NaN);
    }

    public double determineCourseRelativeToHorizon() {
        Optional<Line> middle = determineLaneMiddle();

        if (!middle.isPresent()) {
            return Double.NaN;
        }

        Line middleLine = middle.get();
        Point2D middleAtHorizon = middleLine.topMost();
        double middleAtHorizonX = middleAtHorizon.getX();


        double maxX = 600; // width of image;

        double courseAbs = (middleAtHorizonX / maxX);

        double courseMaxLeft = 0.7; // 0.7 = auto rijdt max links tov horizon
        double courseMiddle = 0.5; // 0.5 = midden
        double courseMaxRight = 0.2; // 0.2 = auto rijdt max rechts tov horizon


        double course = 0;

        if (courseAbs > courseMiddle) {
            // course left
            double rangeLeft = courseMaxLeft - courseMiddle;
            double courseLeftPercentage = 100 * (courseAbs - courseMiddle) / rangeLeft;
            if (courseLeftPercentage > 100) {
                courseLeftPercentage = 100;
            }
            course = -courseLeftPercentage;
        }

        if (courseAbs < courseMiddle) {
            // course right
            double rangeRight = courseMiddle - courseMaxRight;
            double courseRightPercentage = 100 * (courseMiddle - courseAbs) / rangeRight;
            course = courseRightPercentage;
        }

        return course;

    }

    public Optional<Line> determineLaneMiddle() {
        if (!lane.getLeftBoundary().isPresent() || !lane.getRightBoundary().isPresent()) {
            return Optional.empty();
        }
        Line left = lane.getLeftBoundary().get();
        Line right = lane.getRightBoundary().get();

        Optional<Line> extendedBase = determineBase();
        return extendedBase.map(base -> {
            Point2D infHorizon = left.intersect(right).orElseThrow(() -> new RuntimeException("Left and Right boundary do not intersect ..."));
            Point2D baseMiddle = base.pointAt(0.5);

            return new Line(baseMiddle, infHorizon);
        });
    }

    public Optional<Line> determineBase() {
        if (!lane.getLeftBoundary().isPresent() || !lane.getRightBoundary().isPresent()) {
            return Optional.empty();
        }

        Line left = lane.getLeftBoundary().get();
        Line right = lane.getRightBoundary().get();

        Point2D leftLow = left.bottomMost();
        Point2D leftLeft = left.leftMost();
        Point2D rightLow = right.bottomMost();
        Point2D rightRight = right.rightMost();

        Point2D lowest = leftLow.getY() > rightLow.getY() ? leftLow : rightLow;

        Line base = new Line(new Point(leftLeft.getX(), lowest.getY()), new Point(rightRight.getX(), lowest.getY()));

        Point2D leftIntersect = base.intersect(left).orElseThrow(() -> new RuntimeException("Left does not intersect base ..."));
        Point2D rightIntersect = base.intersect(right).orElseThrow(() -> new RuntimeException("Right does not intersect base ..."));

        return of(new Line(leftIntersect, rightIntersect));
    }
}