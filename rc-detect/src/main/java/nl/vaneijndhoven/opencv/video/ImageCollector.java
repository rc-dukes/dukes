package nl.vaneijndhoven.opencv.video;

import java.util.HashMap;
import java.util.Map;

import org.opencv.core.Mat;

/**
 * a set of images to be used for display/debugging
 */
public class ImageCollector {

  public static class Image {
    public static String ext = ".jpg";
    Mat frame;
    byte[] imageBytes;

    public Mat getFrame() {
      return frame;
    }

    public void setFrame(Mat frame) {
      this.frame = frame;
      this.imageBytes = ImageUtils.mat2ImageBytes(frame, ext);
    }
    /**
     * construct me
     * 
     * @param frame
     */
    public Image(Mat frame) {
      this.setFrame(frame);
    }
  }

  private Map<String, Image> images = new HashMap<String, Image>();

  /**
   * construct me
   */
  public ImageCollector() {
    edges(null);
    lines(null);
    originalFrame(null);
    startLight(null);
    morph(null);
    mask(null);
  }

  public void edges(Mat frame) {
    images.put("edges",new Image(frame));
  }

  public void lines(Mat frame) {
    images.put("lines",new Image(frame));
  }

  public void startLight(Mat frame) {
    images.put("start", new Image(frame));
  }

  public void mask(Mat frame) {
    images.put("mask", new Image(frame));
  }

  public void morph(Mat frame) {
    images.put("morph", new Image(frame));
  }

  public void originalFrame(Mat frame) {
    getImages().put("original",new Image(frame));
  }

  public byte[] edges() {
    return getImages().get("edges").imageBytes;
  }

  public byte[] lines() {
    return getImages().get("lines").imageBytes;
  }

  public byte[] startLight() {
    return getImages().get("start").imageBytes;
  }

  public byte[] mask() {
    return getImages().get("mask").imageBytes;
  }

  public byte[] morph() {
    return getImages().get("morph").imageBytes;
  }

  public byte[] originalFrame() {
    return getImages().get("original").imageBytes;
  }

  /**
   * @return the images
   */
  public Map<String, Image> getImages() {
    return images;
  }

}
