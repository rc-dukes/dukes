package org.rcdukes.video;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.core.Mat;

/**
 * Wrapper for openCV Images
 * @author wf
 *
 */
public class Image {
  public static transient final String DATE_FORMAT="HH:mm:ss.SSS";
  public static transient final DateFormat dateFormat=new SimpleDateFormat(DATE_FORMAT);

  public static boolean debug=true;
  public static String ext = ".jpg";
  String name;
  Mat frame;
  byte[] imageBytes;
  private int frameIndex;
  long milliTimeStamp;
  private Date timeStamp;
  
  /**
   * @return the frameIndex
   */
  public int getFrameIndex() {
    return frameIndex;
  }

  /**
   * @param frameIndex the frameIndex to set
   */
  public void setFrameIndex(int frameIndex) {
    this.frameIndex = frameIndex;
  }
  
  /**
   * get the openCV Mat
   * @return - the frame
   */
  public Mat getFrame() {
    return frame;
  }

  /**
   * set the openCV frame
   * @param frame
   */
  public void setFrame(Mat frame) {
    this.frame = frame;
    if (frame!=null)
      this.imageBytes = ImageUtils.mat2ImageBytes(frame, ext);
  }
  
  /**
   * construct me
   * 
   * @param frame
   * @param milliTimeStamp 
   * @param frameIndex 
   */
  public Image(Mat frame, String name,int frameIndex, long milliTimeStamp) {
    this.setFrame(frame);
    this.name=name;
    this.setFrameIndex(frameIndex);
    this.milliTimeStamp=milliTimeStamp;
    this.timeStamp=new Date(milliTimeStamp);
  }

  /**
   * make sure we release our frame when we are garbage collected
   */
  public void finalize() {
    if (debug) {
      String msg=String.format("releasing image %s %d of %s",name,getFrameIndex(),dateFormat.format(timeStamp));
      System.out.println(msg);     
    }
    // this.frame.release();
    // this.frame=null;
  }

  
}