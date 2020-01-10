package nl.vaneijndhoven.objects;

import java.util.Optional;

import org.rcdukes.geometry.Line;

public class Lane {

    Optional<Line> leftBoundary;
    Optional<Line> rightBoundary;

    public Lane(Optional<Line> leftBoundary, Optional<Line> rightBoundary) {
        this.leftBoundary = leftBoundary;
        this.rightBoundary = rightBoundary;
    }

    public Optional<Line> getLeftBoundary() {
        return leftBoundary;
    }

    public Optional<Line> getRightBoundary() {
        return rightBoundary;
    }
}
