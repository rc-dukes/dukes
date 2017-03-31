package nl.vaneijndhoven.dukes.cooter;

public interface SteeringMap {

//    public static final int WHEEL_MAX_LEFT = 130;
//    public static final int WHEEL_CENTER = 163;
//    public static final int WHEEL_STEP_SIZE = 5;
//    public static final int WHEEL_MAX_RIGHT = 190;

    int center();
    int stepSize();

    int maxLeft();
    int maxRight();
}
