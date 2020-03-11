package org.rcdukes.geometry;

import java.util.Optional;

/**
 * a lane
 */
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
