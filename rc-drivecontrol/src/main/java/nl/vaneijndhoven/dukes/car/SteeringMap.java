package nl.vaneijndhoven.dukes.car;

/**
 * configures the Steering servo parameters
 *
 */
public interface SteeringMap extends ServoMap {

    int center();
    int stepSize();

    int maxLeft();
    int maxRight();
}
