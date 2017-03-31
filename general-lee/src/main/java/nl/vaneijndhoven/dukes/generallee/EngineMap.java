package nl.vaneijndhoven.dukes.generallee;

public class EngineMap implements nl.vaneijndhoven.dukes.car.EngineMap {

    public static final int SPEED_ZERO = 130;
    public static final int SPEED_STEP_SIZE = 1;

    public static final int MIN_SPEED_REVERSE = SPEED_ZERO - 9;
    public static final int MIN_SPEED_FORWARD = SPEED_ZERO + 8;

    public static final int MAX_SPEED_REVERSE = SPEED_ZERO - 50;
    public static final int MAX_SPEED_FORWARD = SPEED_ZERO + 90;


    @Override
    public int neutral() {
        return SPEED_ZERO;
    }

    @Override
    public int stepSize() {
        return SPEED_STEP_SIZE;
    }

    @Override
    public int minReverse() {
        return MIN_SPEED_REVERSE;
    }

    @Override
    public int maxReverse() {
        return MAX_SPEED_REVERSE;
    }

    @Override
    public int minForward() {
        return MIN_SPEED_FORWARD;
    }

    @Override
    public int maxForward() {
        return MAX_SPEED_FORWARD;
    }
}
