package nl.vaneijndhoven.dukes.car;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import nl.vaneijndhoven.dukes.car.EngineMap;
import nl.vaneijndhoven.dukes.car.Car;

/**
 * Car speed handling
 *
 */
class SpeedHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SpeedHandler.class);

    private static int currentSpeed;
    private Car car;

    public SpeedHandler(Car car) {
        this.car = car;
        currentSpeed = car.getEngine().getEngineMap().neutral();
    }

    public void setCurrentSpeedToZero() {
        currentSpeed = car.getEngine().getEngineMap().neutral();
    }

    void handleMotor(JsonObject messageBody) {
        LOG.debug("Received message for motor: " + messageBody);
        String speed = messageBody.getString("speed");

        EngineMap engineMap = car.getEngine().getEngineMap();

        if ("up".equals(speed)) {
            currentSpeed = currentSpeed + engineMap.stepSize();

            // help speed get over the dead zone around SPEED_ZERO
            if (currentSpeed > engineMap.neutral() && currentSpeed < engineMap.minForward()) {
                currentSpeed = engineMap.minForward();
            } else if (currentSpeed > engineMap.minReverse() && currentSpeed < engineMap.neutral()) {
                currentSpeed = engineMap.neutral();
            }

            if (currentSpeed > engineMap.maxForward()) {
                currentSpeed = engineMap.maxForward();
            }

            setMotorSpeedWrapper(currentSpeed);

        } else if ("down".equals(speed)) {
            currentSpeed = currentSpeed - engineMap.stepSize();

            // help speed get over the dead zone around SPEED_ZERO
            if (currentSpeed < engineMap.neutral() && currentSpeed > engineMap.minReverse()) {
                currentSpeed = engineMap.minReverse();
            } else if (currentSpeed < engineMap.minForward() && currentSpeed > engineMap.neutral()) {
                currentSpeed = engineMap.neutral();
            }

            if (currentSpeed < engineMap.maxReverse()) {
                currentSpeed = engineMap.maxReverse();
            }

            setMotorSpeedWrapper(currentSpeed);

        } else if ("stop".equals(speed)) {
            currentSpeed = engineMap.neutral();
            car.stop();
        } else if ("brake".equals(speed)) {
            performBrake();
        }


    }


    private void performBrake() {
        LOG.debug("engaging braking sequence");

        try {

            setSpeedDirect("-1");

            Thread.sleep(100);
            setSpeedDirect("-2");

            Thread.sleep(300);
            changeSpeed("-1");

            Thread.sleep(500);
            changeSpeed("stop");

            Thread.sleep(1000);
            changeSpeed("stop");

        } catch (InterruptedException e) {
            LOG.error("Error in brake sequence", e);
        }

    }


    private void setSpeedDirect(String speed) {
        JsonObject message = new JsonObject();
        message.put("speed", speed);
        handleSpeedDirect(message);
    }

    private void changeSpeed(String speed) {
        JsonObject message = new JsonObject();
        message.put("speed", speed);
        handleMotor(message);
    }

    void handleSpeedDirect(JsonObject messageBody) {
        LOG.debug("Received direct message for speed: " + messageBody);
        String speed = messageBody.getString("speed");

        double speedPercentage = Double.valueOf(speed);

        double targetSpeed;

        EngineMap engineMap = car.getEngine().getEngineMap();

        if (speedPercentage == 0) {
            targetSpeed = 0d;
        } else if (speedPercentage > 0) {
            double diff = ((Math.abs(engineMap.maxForward() - engineMap.minForward()) * Math.abs(speedPercentage)) / 100);
//                    LOG.debug("speedPercentage > 0, diff = " + diff);
            targetSpeed = engineMap.minForward() + diff;
        } else {
            double diff = ((Math.abs(engineMap.maxReverse() - engineMap.minReverse()) * Math.abs(speedPercentage)) / 100);
//                    LOG.debug("speedPercentage < 0, diff = " + diff);
            targetSpeed = engineMap.minReverse() - diff;
        }

        LOG.debug("set speed percentage to " + speedPercentage + ", target speed to " + targetSpeed);

        currentSpeed = new Double(targetSpeed).intValue();
        setMotorSpeedWrapper(currentSpeed);
    }

    private void setMotorSpeedWrapper(int speed) {
        // LOG.debug("set motor speed - currentValue = " + currentSpeed);

        EngineMap engineMap = car.getEngine().getEngineMap();

        // reverse speed because motor was initially connected the wrong way around
        int diff = Math.abs(engineMap.neutral() - speed);
        if (speed < engineMap.neutral()) {
            speed = engineMap.neutral() + Math.abs(diff);
        } else {
            speed = engineMap.neutral() - Math.abs(diff);
        }

        car.drive(speed);
    }

}
