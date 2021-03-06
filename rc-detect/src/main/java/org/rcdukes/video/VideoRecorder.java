package org.rcdukes.video;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;

/**
 * record videos
 * @author wf
 *
 */
public class VideoRecorder {
 
  String name;
  private VideoWriter save;
  private Size frameSize;
  private double fps;

  boolean started;
  boolean isColor;
  public static String exts[]= {".mov",".avi",".mpg"};
  private String ext=".avi";
  private String path;
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
    setPath(ImageUtils.filePath(name+"_"+FOURCC,getExt()));
    save = new VideoWriter(getPath(),fourcc, this.fps, this.frameSize, isColor);
    started=true;
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

  /**
   * @return the path
   */
  public String getPath() {
    return path;
  }

  /**
   * @param path the path to set
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * @return the ext
   */
  public String getExt() {
    return ext;
  }

  /**
   * @param ext the ext to set
   */
  public void setExt(String ext) {
    this.ext = ext;
  }
  
}
