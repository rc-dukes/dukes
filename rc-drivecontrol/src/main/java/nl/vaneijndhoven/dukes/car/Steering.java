package nl.vaneijndhoven.dukes.car;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Steering {

    SteeringMap steeringMap;

    private static final int SERVOBLASTER_ID_WHEEL = 2; // GPIO-18

    private static final Logger LOG = LoggerFactory.getLogger(Steering.class);

    public Steering(SteeringMap steeringMap) {
        this.steeringMap = steeringMap;
    }

    public void center() {
        boolean force = false;
        setWheelPosition(steeringMap.center(), force);
    }

    public void forceCenter() {
        boolean force = true;
        setWheelPosition(steeringMap.center(), force);
    }

    public static void setWheelPosition(int position) {
        boolean force = false;
        setWheelPosition(position, force);
    }

    private static void setWheelPosition(int position, boolean force) {
        if (!Command.powerIsOn() && !force) {
            LOG.debug("Not setting servo value; power is off and force is false.");
            return;
        }

        LOG.debug("Setting servo to value " + position);
        Command.servoBlaster(SERVOBLASTER_ID_WHEEL, position);
    }

    public SteeringMap getSteeringMap() {
        return steeringMap;
    }

}
