package org.rcdukes.car;

/**
 * @author wf
 *
 */
public class Led extends Servo {
  private LedMap ledMap;

  /**
   * create me from the given LedMap
   * @param ledMap
   */
  public Led(LedMap ledMap) {
    super(ledMap);
    this.ledMap=ledMap;
  }
  
  public void statusLedOn() {
    LOG.debug("Setting status led ON");
    super.setServo(ledMap.ledOn());
  }

  public void statusLedOff() {
    LOG.debug("Setting status led OFF");
    super.setServo(ledMap.ledOff());
  }

}
