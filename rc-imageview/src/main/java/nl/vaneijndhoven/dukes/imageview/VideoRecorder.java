package nl.vaneijndhoven.dukes.imageview;

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

  String name;
  private VideoWriter save;
  private Size frameSize;
  private double fps;
  String path;
  boolean started;
  boolean isColor;
  
  /**
   * construct me
   * @param name - the name of the video - a timestamp will be added
   * @param isColor - true if the video is a color video
   */
  public VideoRecorder(String name, boolean isColor) {
    this.name=name;
    this.isColor=isColor;
    Date now = new Date();
    String timestamp=dateFormat.format(now);
    path=String.format("/tmp/%s_%s.mov", name,timestamp);
    started=false;
  }
  
  // https://stackoverflow.com/questions/53158765/record-and-save-video-stream-use-opencv-in-java
  public void start(double fps, Size frameSize) {
    this.fps=fps;
    this.frameSize=frameSize;
    // MJPG, X264, H264, MP4V, AVC1, FMP4
    int fourcc = VideoWriter.fourcc('m', 'p', '4', 'v');
    
    // int fourcc = VideoWriter.fourcc('M', 'J', 'P', 'G');
    // int fourcc = VideoWriter.fourcc('A', 'V', 'C', '1');
    save = new VideoWriter(path,fourcc, this.fps, this.frameSize, isColor);
    started=true;
  }
  
  /**
   * stop the recording
   */
  public void stop() {
    save.release();
    started=false;
  }

  /**
   * record a single frame
   * @param mat - open cv frame to be recorded
   */
  public void recordMat(Mat mat) {
    save.write(mat);
  }
  
}
