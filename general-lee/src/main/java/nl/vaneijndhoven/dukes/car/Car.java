package nl.vaneijndhoven.dukes.car;

public class Car {

    Engine engine;
    Steering steering;

    public Car(Engine engine, Steering steering) {
        this.engine = engine;
        this.steering = steering;
    }

    public void stop() {
        engine.neutral();
        steering.center();
    }

    public void turn(double amount) {
        Command.setWheelPosition((int)amount);
    }

    public void drive(double amount) {
        Command.setMotorSpeed((int)amount);
    }

    public Engine getEngine() {
        return engine;
    }

    public Steering getSteering() {
        return steering;
    }
}
