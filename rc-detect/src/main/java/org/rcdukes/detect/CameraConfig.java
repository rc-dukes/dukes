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
  private double roiw;
  private double fps;
  /**
   * construct me
   */
  public CameraConfig() {
    fps=10.0;
    setRoiw(0.55);
    setRoih(0.45);
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
   * @return the roiw
   */
  public double getRoiw() {
    return roiw;
  }

  /**
   * @param roiw the roiw to set
   */
  public void setRoiw(double roiw) {
    this.roiw = roiw;
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
