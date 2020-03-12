package org.rcdukes.video;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.rcdukes.video.ImageUtils.CVColor;

/**
 * Wrapper for openCV Images
 * 
 * @author wf
 *
 */
public class Image {
  public static transient final String DATE_FORMAT = "HH:mm:ss.SSS";
  public static transient final DateFormat dateFormat = new SimpleDateFormat(
      DATE_FORMAT);

  public static boolean debug = false;
  public static String ext = ".jpg";
  private String name;
  Mat frame;
  private byte[] imageBytes;
  private int frameIndex;
  private long milliTimeStamp;
  private Date timeStamp;

  /**
   * @return the frameIndex
   */
  public int getFrameIndex() {
    return frameIndex;
  }

  /**
   * @param frameIndex
   *          the frameIndex to set
   */
  public void setFrameIndex(int frameIndex) {
    this.frameIndex = frameIndex;
  }

  /**
   * get the openCV Mat
   * 
   * @return - the frame
   */
  public Mat getFrame() {
    return frame;
  }

  /**
   * set the openCV frame
   * 
   * @param frame
   */
  public void setFrame(Mat frame) {
    this.frame = frame;
    if (frame != null)
      refresh();
  }

  public void refresh() {
    this.setImageBytes(ImageUtils.mat2ImageBytes(frame, ext));
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the imageBytes
   */
  public byte[] getImageBytes() {
    return imageBytes;
  }

  /**
   * @param imageBytes
   *          the imageBytes to set
   */
  public void setImageBytes(byte[] imageBytes) {
    this.imageBytes = imageBytes;
  }

  /**
   * @return the milliTimeStamp
   */
  public long getMilliTimeStamp() {
    return milliTimeStamp;
  }

  /**
   * @param milliTimeStamp
   *          the milliTimeStamp to set
   */
  public void setMilliTimeStamp(long milliTimeStamp) {
    this.milliTimeStamp = milliTimeStamp;
  }

  /**
   * construct me
   * 
   * @param frame
   * @param milliTimeStamp
   * @param frameIndex
   */
  public Image(Mat frame, String name, int frameIndex, long milliTimeStamp) {
    this.setFrame(frame);
    this.setName(name);
    this.setFrameIndex(frameIndex);
    this.setMilliTimeStamp(milliTimeStamp);
    this.timeStamp = new Date(milliTimeStamp);
  }

  /**
   * make sure we release our frame when we are garbage collected
   */
  public void finalize() {
    if (debug) {
      String msg = String.format("releasing %s", this.debugInfo());
      System.out.println(msg);
    }
    // this.frame.release();
    // this.frame=null;
  }

  /**
   * add my frameIndex as debug info to the given frame
   * 
   * @param frame
   *          - the target frame
   * @param into
   *          the info to add
   */
  public void addImageInfo(Mat frame, String info) {
    int fontFace = Core.FONT_HERSHEY_SIMPLEX;
    int fontScale = 1;
    Scalar color = CVColor.dodgerblue;
    Point pos = new Point(frame.width() - 100, 25);
    Imgproc.putText(frame, info, pos, fontFace, fontScale, color);
  }

  /**
   * add the given info to my frame
   * 
   * @param info
   */
  public void addImageInfo(String info) {
    this.addImageInfo(this.frame, info);
    this.refresh();
  }

  /**
   * add my image info
   */
  public void addImageInfo() {
    String text = String.format("%5d", getFrameIndex());
    this.addImageInfo(text);
  }

  public String debugInfo() {
    String info = String.format("image %s %dx%d %d of %s", getName(),
        frame.width(), frame.height(), getFrameIndex(),
        dateFormat.format(timeStamp));
    return info;
  }

}