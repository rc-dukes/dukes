package nl.vaneijndhoven.dukes.car;

/**
 * configures LED settings
 * @author wf
 *
 */
public interface LedMap extends ServoMap {
  int ledOff();
  int ledOn();
}
