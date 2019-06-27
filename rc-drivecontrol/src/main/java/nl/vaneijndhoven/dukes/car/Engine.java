package nl.vaneijndhoven.dukes.car;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Engine / Motor handling
 */
public class Engine {

    private static final Logger LOG = LoggerFactory.getLogger(Engine.class);

    private static final int SERVOBLASTER_ID_MOTOR = 1; // GPIO-17

    private EngineMap mapping;

    /**
     * create an Engine with the given mapping
     * @param mapping
     */
    public Engine(EngineMap mapping) {
        this.mapping = mapping;
    }

    public void neutral() {
        boolean force = false;
        neutral(force);
    }

    public void forceInNeutral() {
        boolean force = true;
        neutral(force);
    }

    public void neutral(boolean force) {
        setSpeed(mapping.neutral(), force);
    }

    public void setSpeed(int speed) {
        boolean force = false;
        setSpeed(speed, force);
    }

    private void setSpeed(int speed, boolean force) {
        if (!Command.powerIsOn() && !force) {
            LOG.debug("Not setting motor value; power is off and force is false.");
            return;
        }

        LOG.debug("Setting motor to value " + speed);
        Command.servoBlaster(SERVOBLASTER_ID_MOTOR, speed);
    }

    public void setMapping(EngineMap mapping) {
        this.mapping = mapping;
    }

    public EngineMap getEngineMap() {
        return mapping;
    }
}
