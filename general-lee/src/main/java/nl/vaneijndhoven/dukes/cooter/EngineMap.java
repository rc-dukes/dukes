package nl.vaneijndhoven.dukes.cooter;

public interface EngineMap {

//    public static final int SPEED_ZERO = 130;
//    public static final int SPEED_STEP_SIZE = 1;
//
//    public static final int MIN_SPEED_REVERSE = SPEED_ZERO - 9;
//    public static final int MIN_SPEED_FORWARD = SPEED_ZERO + 8;
//
//    public static final int MAX_SPEED_REVERSE = SPEED_ZERO - 50;
//    public static final int MAX_SPEED_FORWARD = SPEED_ZERO + 90;

    public int neutral();
    public int stepSize();
    public int minReverse();
    public int maxReverse();
    public int minForward();
    public int maxForward();

}
