package org.rcdukes.video;

import java.util.HashMap;
import java.util.Map;

import org.opencv.core.Mat;

/**
 * a set of images to be used for display/debugging
 */
public class ImageCollector {
  public static enum ImageType {
    camera, edges, birdseye, lines, startlight, mask, morph
  }
  private Map<ImageType, Image> images = new HashMap<ImageType, Image>();

  /**
   * construct me
   */
  public ImageCollector() {
    // @FIXME - load default images here ...
    // this.addImage(null,ImageType.edges);
  }
  
  /**
   * add the given image
   * @param frame - the frame to add
   * @param - the imageType to use
   * @return - the image
   */
  public Image createImage(Mat frame,ImageType imageType) {
    long milliTimeStamp=System.currentTimeMillis();
    int frameIndex=-1; 
    Image originalFrame=getImages().get(ImageType.camera);
    if (originalFrame!=null) {
      milliTimeStamp=originalFrame.milliTimeStamp;
      frameIndex=originalFrame.getFrameIndex();
    }
    Image image=new Image(frame,imageType.name(),frameIndex,milliTimeStamp);
    return image;
  }
   
  /**
   * add the given frame as an image with the given imageType
   * @param frame
   * @param imageType
   * @return - the Image
   */
  public Image addImage(Mat frame, ImageType imageType) {  
    Image image=createImage(frame,imageType);
    images.put(imageType, image);
    return image;
  }

  /**
   * @return the images
   */
  public Map<ImageType, Image> getImages() {
    return images;
  }

  public Image getImage(ImageType imageType) {
    return images.get(imageType);
  }

}
