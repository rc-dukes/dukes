package org.rcdukes.video;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
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
  private VideoInfo info;

  /**
   * construct me for the given number of frames per second
   * 
   * @param fps
   */
  public VideoRecorders(double fps) {
    info = new VideoInfo(fps);
    info.fps = fps;
  }

  public boolean isStarted() {
    return recorders.size() > 0;
  }

  /**
   * toggle the videoInfo state
   * 
   * @return
   */
  public VideoInfo toggle() {
    if (isStarted())
      return stop();
    else
      return start();
  }

  /**
   * start Recording
   */
  public VideoInfo start() {
    for (ImageType imageType : ImageType.values()) {
      VideoRecorder recorder = new VideoRecorder(imageType.name(), info.fps);
      recorders.put(imageType, recorder);
    }
    return info;
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
   * 
   * @param collector
   */
  public void recordFrame(ImageCollector collector) {
    for (ImageType imageType : ImageType.values()) {
      boolean failSafe = false;
      Image image = collector.getImage(imageType, failSafe);
      if (image != null) {
        info.setFrameIndex(image.getFrameIndex());
        this.recordFrame(image.getFrame(), imageType);
      }
    }
  }

  /**
   * Video Information to be used for storing Meta-Information
   * 
   * @author wf
   *
   */
  public static class VideoInfo {
    public double fps;
    public Integer minFrameIndex = null;
    public Integer maxFrameIndex = null;
    public String path;

    /**
     * construct me with given path and frames per second
     * 
     * @param path
     * @param fps
     */
    public VideoInfo(double fps) {
      this.fps = fps;
    }

    /**
     * copy constructor to reset frameIndex
     * 
     * @param info
     */
    public VideoInfo(VideoInfo info) {
      this(info.fps);
    }

    /**
     * set the frameIndex
     * 
     * @param frameIndex
     */
    public void setFrameIndex(int frameIndex) {
      if (minFrameIndex == null)
        minFrameIndex = frameIndex;
      if (maxFrameIndex == null)
        maxFrameIndex = frameIndex;
      minFrameIndex = Math.min(minFrameIndex, frameIndex);
      maxFrameIndex = Math.max(maxFrameIndex, frameIndex);
    }

    /**
     * set the path
     * 
     * @param path
     * @param ext
     */
    public void setPath(String pPath) {
      if (this.path == null && pPath!=null) {
        this.path = getNavigationFile(new File(pPath)).getPath();
      }
    }

    /**
     * get the navigation File for the given file
     * @param file
     * @return - the navigationFile
     */
    public static File getNavigationFile(File file) {
      String graphFilePath = FilenameUtils
          .getBaseName(file.getPath()) + ".json";
      graphFilePath=graphFilePath.replaceAll(".*_mp4v_", "navigation_");
      File graphFile = new File(file.getParent(),graphFilePath);
      return graphFile;
    }
  }

  /**
   * stop Recording
   */
  public VideoInfo stop() {
    Iterator<Entry<ImageType, VideoRecorder>> it = recorders.entrySet()
        .iterator();
    while (it.hasNext()) {
      Entry<ImageType, VideoRecorder> entry = it.next();
      VideoRecorder recorder = entry.getValue();
      info.setPath(recorder.getPath());
      recorder.stop();
      it.remove();
    }
    VideoInfo result = info;
    this.info = new VideoInfo(info);
    return result;
  }
}
