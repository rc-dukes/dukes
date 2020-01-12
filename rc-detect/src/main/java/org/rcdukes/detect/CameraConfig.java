package org.rcdukes.detect;

import org.rcdukes.common.Config;

/**
 * camera configuration to be exchanged with gui
 * @author wf
 *
 */
public class CameraConfig {
  private String source;
  private double roih;
  private double roiy;
  private double fps;
  /**
   * construct me
   */
  public CameraConfig() {
    fps=10.0;
    setRoiy(44);  // 44% offset
    setRoih(100); // full rest height
    try {
      source = Config.getEnvironment().getString(Config.CAMERA_URL);
    } catch (Exception e) {
      org.rcdukes.error.ErrorHandler.getInstance().handle(e);
    }
  }
  
  public long getInterval() {
    return Math.round(1000/fps);
  }

  /**
   * @return the roiy
   */
  public double getRoiy() {
    return roiy;
  }

  /**
   * @param roiy the roiy to set
   */
  public void setRoiy(double roiy) {
    this.roiy = roiy;
  }

  /**
   * @return the roih
   */
  public double getRoih() {
    return roih;
  }

  /**
   * @param roih the roih to set
   */
  public void setRoih(double roih) {
    this.roih = roih;
  }
  
  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public double getFps() {
    return fps;
  }

  public void setFps(double fps) {
    this.fps = fps;
  }

}
