package nl.vaneijndhoven.dukes.car;

/** 
 * Steering parameters - servo settings
 */
public interface SteeringMap {
    int center();
    int stepSize();

    int maxLeft();
    int maxRight();
}
