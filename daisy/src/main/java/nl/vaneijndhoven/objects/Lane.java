package nl.vaneijndhoven.objects;

import nl.vaneijndhoven.geometry.Line;

import java.util.Optional;

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
