package nl.vaneijndhoven.navigation.plot;

public class PathPlanner {

    private LaneOrientation laneOrientation;

    public PathPlanner(LaneOrientation laneOrientation) {
        this.laneOrientation = laneOrientation;
    }

    public double determineDeviation() {
        return laneOrientation.determineCurrentAngle();
    }
}
