package org.rcdukes.video;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.Mat;
import org.rcdukes.video.ImageCollector.ImageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;


/**
 * a set of images to be used for display/debugging
 */
public class ImageCollector {
  public static enum ImageType {
    camera, edges, birdseye, lines, startlight, mask, morph, simulator
  }

  protected static final Logger LOG = LoggerFactory
      .getLogger(ImageCollector.class);

  private Map<ImageType, Image> images = new HashMap<ImageType, Image>();
  private static Map<ImageType, Image> testImages = new HashMap<ImageType, Image>();
  private Map<ImageType, List<ObservableEmitter<Image>>> emitterMap=new HashMap<ImageType,List<ObservableEmitter<Image>>>();
  private String ext;
  public static String[] testImagePaths = {
      "images/640px-4_lane_highway_roads_in_India_NH_48_Karnataka_3",
      "images/640px-4_lane_highway_roads_in_India_NH_48_Karnataka_3",
      "images/640px-4_lane_highway_roads_in_India_NH_48_Karnataka_3",
      "images/640px-4_lane_highway_roads_in_India_NH_48_Karnataka_3",
      "images/640px-4_lane_highway_roads_in_India_NH_48_Karnataka_3",
      "images/640px-4_lane_highway_roads_in_India_NH_48_Karnataka_3",
      "images/640px-4_lane_highway_roads_in_India_NH_48_Karnataka_3",
      "images/640px-4_lane_highway_roads_in_India_NH_48_Karnataka_3" };

  /**
   * supply a default testImage
   * 
   * @return - the bytes of the test Image
   */
  public Image getTestImage(ImageType imageType) {
    if (!testImages.containsKey(imageType)) {
      String testImagePath = testImagePaths[imageType.ordinal()] + ext;
      try {
        Mat frame = ImageUtils.fromResource(this.getClass(), testImagePath);
        Image image = new Image(frame, imageType.name(), 0,
            System.currentTimeMillis());
        testImages.put(imageType, image);
      } catch (IOException e) {
        LOG.trace(e.getMessage());
      }
    }
    Image testImage = testImages.get(imageType);
    return testImage;
  }

  /**
   * default constructor
   */
  public ImageCollector() {
    // construct me for preloaded jpg images ...
    this(".jpg", true);
  }

  /**
   * construct me
   */
  public ImageCollector(String ext, boolean preLoadTestImages) {
    this.ext = ext;
    if (preLoadTestImages) {
      for (ImageType imageType : ImageType.values()) {
        this.getTestImage(imageType);
      }
    }
  }

  /**
   * add the given image
   * 
   * @param frame
   *          - the frame to add
   * @param -
   *          the imageType to use
   * @return - the image
   */
  public Image createImage(Mat frame, ImageType imageType) {
    long milliTimeStamp = System.currentTimeMillis();
    int frameIndex = -1;
    Image originalFrame = getImages().get(ImageType.camera);
    if (originalFrame != null) {
      milliTimeStamp = originalFrame.getMilliTimeStamp();
      frameIndex = originalFrame.getFrameIndex();
    }
    Image image = new Image(frame, imageType.name(), frameIndex,
        milliTimeStamp);
    return image;
  }

  /**
   * add the given frame as an image with the given imageType
   * 
   * @param frame
   * @param imageType
   * @return - the Image
   */
  public Image addImage(Mat frame, ImageType imageType) {
    Image image = createImage(frame, imageType);
    this.addImage(image, imageType);
    return image;
  }

  /**
   * add the given image with the given Image type
   * 
   * @param image
   * @param imageType
   * @return
   */
  public Image addImage(Image image, ImageType imageType) {
    images.put(imageType, image);
    if (this.emitterMap.containsKey(imageType)) {
      List<ObservableEmitter<Image>> emitterList = emitterMap.get(imageType);
      for (ObservableEmitter<Image> emitter:emitterList)
        emitter.onNext(image);
    }
    return image;
  }

  /**
   * @return the images
   */
  protected Map<ImageType, Image> getImages() {
    return images;
  }

  /**
   * get the image for the given imageType
   * 
   * @param imageType
   * @param failSafe
   *          - if true return a test Image if the image is not available
   * @return the Image
   */
  public Image getImage(ImageType imageType, boolean failSafe) {
    Image image = null;
    if (images.containsKey(imageType))
      image = images.get(imageType);
    if (image == null && failSafe)
      image = testImages.get(imageType);
    return image;
  }
  
  
  /**
   * create an observable for the given Image type
   * @param imageType
   * @return the observable
   */
  public Observable<Image> createObservable(ImageType imageType) {
    Observable<Image> imageObservable=Observable.create(emitter->{
       if (!emitterMap.containsKey(imageType)) {
         emitterMap.put(imageType, new ArrayList<ObservableEmitter<Image>>());
       }
       List<ObservableEmitter<Image>> emitterList = emitterMap.get(imageType);
       emitterList.add(emitter);     
    });
    return imageObservable;
  }

  /**
   * write the images
   */
  public void writeImages() {
    for (ImageType imageType : ImageType.values()) {
      String filepath=ImageUtils.filePath(imageType.name(), ".jpg");
      Image image=this.getImage(imageType, false);
      if (image!=null)
        ImageUtils.writeImageToFilepath(image.getFrame(), filepath);
    }
    
  }

}
