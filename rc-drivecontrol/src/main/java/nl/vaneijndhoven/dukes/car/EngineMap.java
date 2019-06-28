package nl.vaneijndhoven.dukes.car;

/**
 * Servo settings for the Engine
 *
 */
public interface EngineMap extends ServoMap {

    public int neutral();
    public int stepSize();
    public int minReverse();
    public int maxReverse();
    public int minForward();
    public int maxForward();

}
