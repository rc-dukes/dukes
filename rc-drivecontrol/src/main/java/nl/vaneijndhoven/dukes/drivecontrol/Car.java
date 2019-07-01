package nl.vaneijndhoven.dukes.drivecontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vaneijndhoven.dukes.car.Engine;
import nl.vaneijndhoven.dukes.car.Led;
import nl.vaneijndhoven.dukes.car.ServoBlaster;
import nl.vaneijndhoven.dukes.car.ServoCommand;
import nl.vaneijndhoven.dukes.car.Steering;

/**
 * The car to be remotely controlled
 */
public class Car {
  private static final Logger LOG = LoggerFactory.getLogger(Car.class);
  public static ServoCommand servoCommand=null;
  private boolean powerIsOn = false;
  Engine engine;
  Steering steering;
  Led led;

  /**
   * get a new car as configured -but as a singleton
   */
  private Car() {
    // if nobody configured the servoCommand
    // then we'll use the default ServoBlaster
    if (servoCommand==null) 
      servoCommand = new ServoBlaster();
    engine=new Engine(this,new EngineMap(servoCommand));
    steering=new Steering(this,new SteeringMap(servoCommand));
    led=new Led(new LedMap(servoCommand));
  }

  /**
   * configure me from an engine, a steering and an Led
   * 
   * @param engine
   *          - the engine
   * @param steering
   *          - the steering
   */
  public void configure(Engine engine, Steering steering, Led led) {
    this.engine = engine;
    this.steering = steering;
    this.led = led;
  }
  
  /**
   * set the power on
   */
  public void setPowerOn() {
    if (!powerIsOn) {
      LOG.info("Setting power ON");
      powerIsOn = true;
      led.statusLedOn();
    }
  }

  public void setPowerOff() {
    if (powerIsOn) {
      LOG.info("Setting power OFF");
      // stop();
      powerIsOn = false;
      led.statusLedOff();
    }
  }

  public boolean powerIsOn() {
    return powerIsOn;
  }

  public void stop() {
    engine.forceInNeutral();
    steering.forceCenter();
    led.statusLedOff();
  }

  public void turn(double position) {
    steering.setWheelPosition((int) position);
  }

  public void drive(double speed) {
    engine.setSpeed((int) speed);
  }

  public Engine getEngine() {
    return engine;
  }

  public Steering getSteering() {
    return steering;
  }
  
  public Led getLed() {
    return led;
  }
  
  private static Car instance=null;
  
  /**
   * get the car instance
   * @return - the car instance
   */
  public static Car getInstance() {
    if (instance==null)
      instance=new Car();
    return instance;
  }

  public static void resetInstance() {
    instance=null;
  }
  
}
