package nl.vaneijndhoven.dukes.drivecontrol;

/**
 * steering map which is vehicle dependent
 *
 */
public class SteeringMap implements nl.vaneijndhoven.dukes.car.SteeringMap {

    public static final int WHEEL_CENTER = 163;
    public static final int WHEEL_STEP_SIZE = 5;
    public static final int WHEEL_MAX_LEFT = 130;
    public static final int WHEEL_MAX_RIGHT = 190;

    @Override
    public int center() {
        return WHEEL_CENTER;
    }

    @Override
    public int stepSize() {
        return WHEEL_STEP_SIZE;
    }

    @Override
    public int maxLeft() {
        return WHEEL_MAX_LEFT;
    }

    @Override
    public int maxRight() {
        return WHEEL_MAX_RIGHT;
    }
}
