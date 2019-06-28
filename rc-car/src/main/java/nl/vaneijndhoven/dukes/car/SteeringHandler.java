package nl.vaneijndhoven.dukes.car;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import nl.vaneijndhoven.dukes.car.SteeringMap;
import nl.vaneijndhoven.dukes.drivecontrol.Car;

class SteeringHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SteeringHandler.class);

    private static int currentWheelPosition;
    private Car car;

    SteeringHandler(Car car) {
        this.car = car;
        this.currentWheelPosition = car.getSteering().getSteeringMap().center();
    }

    void handleServoDirect(JsonObject messageBody) {
        LOG.debug("Received direct message for servo: " + messageBody);
        String position = messageBody.getString("position");

        int positionPercentage = Double.valueOf(position).intValue();

        SteeringMap steeringMap = car.getSteering().getSteeringMap();

        if (positionPercentage < 5) {
            int range = steeringMap.center() - steeringMap.maxLeft();
            currentWheelPosition = steeringMap.center() - ((range * Math.abs(positionPercentage))/100);
            LOG.debug("directPos = " + position + ", range = " + range + ", newPos = " + currentWheelPosition);
        } else if (positionPercentage > 5) {
            int range = steeringMap.maxRight() - steeringMap.center();
            currentWheelPosition = steeringMap.center() + ((range * Math.abs(positionPercentage))/100);
            LOG.debug("directPos = " + position + ", range = " + range + ", newPos = " + currentWheelPosition);
        } else {
            currentWheelPosition = steeringMap.center();
        }

        if (currentWheelPosition < steeringMap.maxLeft()) {
            currentWheelPosition = steeringMap.maxLeft();
        }
        if (currentWheelPosition > steeringMap.maxRight()) {
            currentWheelPosition = steeringMap.maxRight();
        }
        LOG.debug("about to set current wheel pos to " + currentWheelPosition);
//        Command.setWheelPosition(currentWheelPosition);
        car.turn(currentWheelPosition);
    }

    void handleServo(JsonObject messageBody) {
        LOG.debug("Received message for servo: " + messageBody);
        String position = messageBody.getString("position");

        SteeringMap steeringMap = car.getSteering().getSteeringMap();

        if ("left".equals(position)) {
            currentWheelPosition = currentWheelPosition - steeringMap.stepSize();
            if (currentWheelPosition < steeringMap.maxLeft()) {
                currentWheelPosition = steeringMap.maxLeft();
            }
        } else if ("right".equals(position)) {
            currentWheelPosition = currentWheelPosition + steeringMap.stepSize();
            if (currentWheelPosition > steeringMap.maxRight()) {
                currentWheelPosition = steeringMap.maxRight();
            }
        } else if ("center".equals(position)) {
            currentWheelPosition = steeringMap.center();
        }
//        Command.setWheelPosition(currentWheelPosition);
        car.turn(currentWheelPosition);
    }

}
