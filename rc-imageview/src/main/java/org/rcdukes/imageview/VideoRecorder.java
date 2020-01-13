package org.rcdukes.imageview;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;

/**
 * record videos
 * @author wf
 *
 */
public class VideoRecorder {
  public static transient final String DATE_FORMAT="yyyy-MM-ddHHmmss";
  public static transient final DateFormat dateFormat=new SimpleDateFormat(DATE_FORMAT);
  public static String path="/tmp";
  
  String name;
  private VideoWriter save;
  private Size frameSize;
  private double fps;

  boolean started;
  boolean isColor;
  public static String exts[]= {".mov",".avi",".mpg"};
  String ext=".avi";
  public static String FOURCCs[]= { "AVC1", "FMP4", "H264", "JPEG","MJPG",  "MP4V","X264","XVID" };
  public static String FOURCC="mp4v";
  
  /**
   * construct me
   * @param name - the name of the video - a timestamp will be added
   * @param fps - frames per second
   */
  public VideoRecorder(String name, double fps) {
    this.name=name;
    this.fps=fps;
    started=false;
  }
  
  // https://stackoverflow.com/questions/53158765/record-and-save-video-stream-use-opencv-in-java
  public void start(Size frameSize, boolean isColor) {
    this.frameSize=frameSize;
    this.isColor=isColor;
    int fourcc = VideoWriter.fourcc(FOURCC.charAt(0), FOURCC.charAt(1), FOURCC.charAt(2), FOURCC.charAt(3)); 
    String videopath=filePath(name+"_"+FOURCC,ext);
    save = new VideoWriter(videopath,fourcc, this.fps, this.frameSize, isColor);
    started=true;
  }
  
  
  /**
   * get a filePath for the given name and extension by adding a timeStamp
   * @param name
   * @param ext
   * @return - the filePath
   */
  public static String filePath(String name, String ext) {
    Date now = new Date();
    String timestamp=dateFormat.format(now);
    String filepath=String.format("%s/%s_%s%s", path,name,timestamp,ext);
    return filepath;
  }
  
  
  /**
   * stop the recording
   */
  public void stop() {
    if (save!=null)
      save.release();
    started=false;
  }

  /**
   * record a single frame
   * @param mat - open cv frame to be recorded
   */
  public void recordMat(Mat mat) {
    if (!started) {
      isColor=mat.channels()>1;
      this.start(mat.size(),isColor);
    }
    save.write(mat);
  }
  
}
