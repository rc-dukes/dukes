package org.rcdukes.car;

/**
 * configures the Steering servo parameters
 *
 */
public interface SteeringMap extends ServoMap {

    int center();
    int stepSize();

    int maxLeft();
    int maxRight();
    
    double maxLeftAngle();
    double maxRightAngle();
}
