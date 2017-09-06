package nl.vaneijndhoven.dukes.car;

public class Car {

    Engine engine;
    Steering steering;

    public Car(Engine engine, Steering steering) {
        this.engine = engine;
        this.steering = steering;
    }

    public void stop() {
        engine.forceInNeutral();
        steering.forceCenter();
    }

    public void turn(double position) {
        steering.setWheelPosition((int)position);
    }

    public void drive(double speed) {
        engine.setSpeed((int)speed);
    }

    public Engine getEngine() {
        return engine;
    }

    public Steering getSteering() {
        return steering;
    }
}
