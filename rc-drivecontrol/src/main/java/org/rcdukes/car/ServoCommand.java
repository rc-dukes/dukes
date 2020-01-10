package org.rcdukes.car;

/**
 * sends servo commands for controlling Servos and LEDs
 *
 */
public interface ServoCommand {
  /**
   * set the servo with the given gpioPin to the given value
   * @param ioId - the input/output id of the servo - the numbering
   * scheme is implementation dependent
   * @param value - the (raw) value to set for the servo
   */
  public void setServo(int ioId, int value);
}
