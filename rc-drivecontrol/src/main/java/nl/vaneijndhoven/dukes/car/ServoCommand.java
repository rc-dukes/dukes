package nl.vaneijndhoven.dukes.car;

/**
 * sends servo commands for controlling Servos and LEDs
 *
 */
public interface ServoCommand {
  public void setServo(int gpioPin, int value);
}
