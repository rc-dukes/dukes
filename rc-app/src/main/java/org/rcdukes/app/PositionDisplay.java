package org.rcdukes.app;

import org.rcdukes.common.ServoPosition;

/**
 * show a Servo Position in the GUI
 * @author wf
 *
 */
public interface PositionDisplay {
  public void showPosition(ServoPosition position);
}
