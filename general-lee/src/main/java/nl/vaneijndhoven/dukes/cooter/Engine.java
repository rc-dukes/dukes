package nl.vaneijndhoven.dukes.cooter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static nl.vaneijndhoven.dukes.cooter.Command.servoBlaster;

public class Engine {

    private static final Logger LOG = LoggerFactory.getLogger(Engine.class);

    private static final int SERVOBLASTER_ID_MOTOR = 1; // GPIO-17

    private EngineMap mapping;

    public Engine(EngineMap mapping) {
        this.mapping = mapping;
    }

    public void neutral() {
        setSpeed(mapping.neutral());
    }

    public static void setSpeed(int speed) {
        if (!Command.powerIsOn()) {
            LOG.debug("Not setting motor value; power is off.");
            return;
        }

        LOG.debug("Setting motor to value " + speed);
        servoBlaster(SERVOBLASTER_ID_MOTOR, speed);
    }

    public void setMapping(EngineMap mapping) {
        this.mapping = mapping;
    }

    public EngineMap getEngineMap() {
        return mapping;
    }
}
