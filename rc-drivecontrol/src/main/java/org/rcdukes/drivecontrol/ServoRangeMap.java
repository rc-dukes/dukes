package org.rcdukes.drivecontrol;

import org.rcdukes.car.ServoPosition;
import org.rcdukes.car.ServoRange;
import org.rcdukes.car.ServoSide;

/**
 * a range map
 * 
 * @author wf
 *
 */
public abstract class ServoRangeMap extends ServoMap
    implements org.rcdukes.car.ServoRangeMap {
  private ServoRange range = null;
  private String unit;
  private String name;

  public ServoRange getRange() {
    return range;
  }

  public void setRange(ServoRange range) {
    if (this.range == null)
      this.currentPosition = range.getZeroPosition();
    this.range = range;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public ServoPosition atPercent(double percent) {
    ServoRange range = this.getRange();
    ServoSide n = range.getSideN();
    ServoSide p = range.getSideP();
    if (this.turnedOrientation) {
      n = range.getSideP();
      p = range.getSideN();
    }
    if (percent == 0)
      return range.getZeroPosition();
    else if (percent < 0) {
      return n.interpolate(percent);
    } else {
      return p.interpolate(percent);
    }
  }

  @Override
  public void step(int servoStep) {
    int spos = this.currentPosition.getServoPos();
    spos+=servoStep*range.getStepSize();
    currentPosition.setServoPos(spos);
    if (range.getSideN().isIn(spos)) {
      currentPosition.setValue(range.getSideN().interpolateValueFromPos(spos));
    } else if (range.getSideP().isIn(spos)) {
      currentPosition.setValue(range.getSideP().interpolateValueFromPos(spos));
    } else {
      this.currentPosition.setValue(0);
    }
  }

  @Override
  public void setZero() {
    this.currentPosition = range.getZeroPosition();
  }

  public String positionInfo() {
    String info = String.format("%5.1f%s (%3d)",
        this.currentPosition.getValue(), this.unit,
        this.currentPosition.getServoPos());
    return info;
  }

  protected void assertGreater(double vmax, double vmin,String maxname, String minname)
      throws Exception {
    boolean ok;
    String cmp="";
    ok=(vmax < vmin); 
    cmp=">=";
    if (ok) {
      String msg = String.format("%s %f should be %s than %s %f", maxname,vmax,
          cmp,minname,vmin);
      throw new Exception(msg);
    }
  }

  protected void checkSide(ServoSide side) throws Exception {
    assertGreater(side.getMax().getValue(), side.getMin().getValue(),
        side.getMax().valueConfig, side.getMin().valueConfig);
    assertGreater(side.getMax().getServoPos(),
        side.getMin().getServoPos(), side.getMax().servoConfig,
        side.getMin().valueConfig);
  }

  /**
   * check the validity of this servoMap
   * 
   * @throws Exception
   */
  protected void check() throws Exception {
    range = this.getRange();
    LOG.info(String.format(
        "%s gpio: %s  %3d - %3d > %3d < %3d - %3d orientation: %s steps:  %3d",
        name, this.gpioPin,
        range.getSideN().getMin().getServoPos(),
        range.getSideN().getMax().getServoPos(),
        range.getZeroPosition().getServoPos(),
        range.getSideP().getMin().getServoPos(),
        range.getSideP().getMax().getServoPos(),
        this.turnedOrientation ? "-" : "+",
        range.getStepSize()));
    assert range.getStepSize() > 0 : "step size should be >0";
    ServoSide sideN = range.getSideN();
    ServoSide sideP = range.getSideP();
    checkSide(sideN);
    checkSide(sideP);
  }

}
