package nl.vaneijndhoven.dukes.imageview;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

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
  /**
   * construct me
   * @param name
   */
  public VideoRecorder(String name) {
    this.name=name;
    Date now = new Date();
    String timestamp=dateFormat.format(now);
    path=String.format("/tmp/%s_%s.mpg", name,timestamp);
    started=false;
  }
  
  // https://stackoverflow.com/questions/53158765/record-and-save-video-stream-use-opencv-in-java
  public void start(double fps, Size frameSize) {
    this.fps=fps;
    this.frameSize=frameSize;
    save = new VideoWriter(path, Videoio.CAP_PROP_FOURCC, this.fps, this.frameSize, true);
    started=true;
  }
  
  public void stop() {
    save.release();
    started=false;
  }

  public void recordMat(Mat mat) {
    save.write(mat);
  }
  
}
