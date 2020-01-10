package org.rcdukes.video;

import java.util.HashMap;
import java.util.Map;

import org.opencv.core.Mat;

/**
 * a set of images to be used for display/debugging
 */
public class ImageCollector {

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
  
  /**
   * create an image
   * @param frame
   * @param name
   * @return - the image
   */
  public Image createImage(Mat frame,String name) {
    long milliTimeStamp=System.currentTimeMillis();
    int frameIndex=-1; 
    Image originalFrame=getImages().get("original");
    if (originalFrame!=null) {
      milliTimeStamp=originalFrame.milliTimeStamp;
      frameIndex=originalFrame.getFrameIndex();
    }
    Image image=new Image(frame,name,frameIndex,milliTimeStamp);
    return image;
  }

  public void edges(Mat frame) {
    images.put("edges",createImage(frame,"edges"));
  }

  public void lines(Mat frame) {
    images.put("lines",createImage(frame,"lines"));
  }

  public void startLight(Mat frame) {
    images.put("start", createImage(frame,"start"));
  }

  public void mask(Mat frame) {
    images.put("mask", createImage(frame,"mask"));
  }

  public void morph(Mat frame) {
    images.put("morph", createImage(frame,"morph"));
  }

  public void originalFrame(Image image) {
    getImages().put("original",image);
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
