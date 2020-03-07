package org.rcdukes.drivecontrol;

import org.rcdukes.car.AdaFruit;
import org.rcdukes.car.Engine;
import org.rcdukes.car.Led;
import org.rcdukes.car.ServoBlaster;
import org.rcdukes.car.ServoCommand;
import org.rcdukes.car.ServoPosition;
import org.rcdukes.car.Steering;
import org.rcdukes.common.Config;
import org.rcdukes.common.Environment;
import org.rcdukes.error.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The car to be remotely controlled
 */
public class Car {
  private static final Logger LOG = LoggerFactory.getLogger(Car.class);
  public static ServoCommand servoCommand = null;
  private boolean powerIsOn = false;
  Engine engine;
  Steering steering;
  Led led;

  /**
   * get a new car as configured -but as a singleton
   */
  private Car() {
    // if nobody configured the servoCommand
    // then we'll use the ServoBlaster
    if (servoCommand == null) {
      Environment env = Config.getEnvironment();
      String servo_CommandConfig="servoblaster";
      try {
        servo_CommandConfig = env.getString(Config.SERVO_COMMAND);
      } catch (Exception e) {
        ErrorHandler.getInstance().handle(e);
      }
      switch (servo_CommandConfig) {
      case "adafruit":
        try {
          servoCommand=new AdaFruit();
          LOG.info("using AdaFruit ServoCommand handling");
        } catch (Exception e) {
          ErrorHandler.getInstance().handle(e);
        }
        break;
      default:
        servoCommand = new ServoBlaster();
      }
    }
    engine = new Engine(this, new EngineMap(servoCommand));
    steering = new Steering(this, new SteeringMap(servoCommand));
    led = new Led(new LedMap(servoCommand));
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

  public void turn(ServoPosition currentWheelPosition) {
    LOG.debug("about to set current wheel pos to " + currentWheelPosition);
    steering.setWheelPosition(currentWheelPosition);
  }

  public void drive(ServoPosition speed) {
    engine.setSpeed(speed);
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

  private static Car instance = null;

  /**
   * get the car instance
   * 
   * @return - the car instance
   */
  public static Car getInstance() {
    if (instance == null)
      instance = new Car();
    return instance;
  }

  public static void resetInstance() {
    instance = null;
  }

}
