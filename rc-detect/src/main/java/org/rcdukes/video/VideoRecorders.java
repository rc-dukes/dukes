package org.rcdukes.video;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.opencv.core.Mat;
import org.rcdukes.video.ImageCollector.ImageType;

/**
 * a set of VideoRecorders which can be started and stopped in Parallel
 * 
 * @author wf
 *
 */
public class VideoRecorders {
  Map<ImageType, VideoRecorder> recorders = new HashMap<ImageType, VideoRecorder>();
  private double fps;

  /**
   * construct me for the given number of frames per second
   * 
   * @param fps
   */
  public VideoRecorders(double fps) {
    this.fps = fps;
  }

  public boolean isStarted() {
    return recorders.size() > 0;
  }

  public void toggle() {
    if (isStarted())
      stop();
    else
      start();
  }

  /**
   * start Recording
   */
  public void start() {
    for (ImageType imageType : ImageType.values()) {
      VideoRecorder recorder = new VideoRecorder(imageType.name(), fps);
      recorders.put(imageType, recorder);
    }
  }

  /**
   * record the given frame if the videorecorder for it's imageType is active
   * 
   * @param frame
   * @param imageType
   */
  public void recordFrame(Mat frame, ImageType imageType) {
    if (recorders.containsKey(imageType) && frame != null) {
      VideoRecorder recorder = recorders.get(imageType);
      recorder.recordMat(frame);
    }
  }

  /**
   * record a frame for the given image collector
   * @param collector
   */
  public void recordFrame(ImageCollector collector) {
    for (ImageType imageType : ImageType.values()) {
      boolean failSafe=false;
      Image image = collector.getImage(imageType, failSafe);
      if (image!=null) {
        this.recordFrame(image.getFrame(),imageType);
      }
    }
  }

  /**
   * stop Recording
   */
  public void stop() {
    Iterator<Entry<ImageType, VideoRecorder>> it = recorders.entrySet()
        .iterator();
    while (it.hasNext()) {
      Entry<ImageType, VideoRecorder> entry = it.next();
      VideoRecorder recorder = entry.getValue();
      recorder.stop();
      it.remove();
    }
  }
}
