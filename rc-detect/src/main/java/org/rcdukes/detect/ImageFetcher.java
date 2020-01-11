package org.rcdukes.detect;

import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.rcdukes.video.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * fetcher for Images - will block at the  given FPS rate
 *
 */
public class ImageFetcher {
  protected static final Logger LOG = LoggerFactory
      .getLogger(ImageFetcher.class);

  public boolean debug=false;
  public static double DEFAULT_FPS=25;
  // OpenCV video capture
  private VideoCapture capture = new VideoCapture();
  private String source;
  protected int frameIndex;
  protected double fps=DEFAULT_FPS; // frames per second
  private long milliTimeStamp;
  private boolean staticImage;

  private Image currentImage=null;

  public double getFps() {
    return fps;
  }

  public void setFps(double fps) {
    this.fps = fps;
  }

  public int getFrameIndex() {
    return frameIndex;
  }
  
  /**
   * @return the staticImage
   */
  public boolean isStaticImage() {
    return staticImage;
  }

  /**
   * @param staticImage the staticImage to set
   */
  public void setStaticImage(boolean staticImage) {
    this.staticImage = staticImage;
  }
  

  /**
   * fetch from the given source
   * 
   * @param source
   *          - the source to fetch from
   */
  public ImageFetcher(String source) {
    this.source = source;
  }

  /**
   * try opening my source
   * 
   * @return true if successful
   */
  public boolean open() {
    boolean ret = this.capture.open(source);
    String ext= FilenameUtils.getExtension(source).toLowerCase();
    setStaticImage(false);
    switch (ext) {
    case "jpg":
    case "png":
    case "jpeg":
      setStaticImage(true);
    }
    frameIndex=0;
    milliTimeStamp = System.nanoTime()/ 1000000;
    return ret;
  }
  
  public void close() {
    this.capture.release();
  }

  /**
   * fetch an image Matrix - will block to make sure Mat is emitted
   * at the frames per second rate configured
   * 
   * @return - the image fetched
   */
  public Image fetch() {
    if (!this.capture.isOpened()) {
      boolean ret = this.open();
      if (!ret) {
        String msg = String.format(
            "Trying to fetch image from unopened VideoCapture and open '%s' failed",
            source);
        throw new IllegalStateException(msg);
      }
    }
    long currentMillis = System.nanoTime()/ 1000000;
    int waitMillis = (int) Math.round(1000/fps);
    waitMillis-=currentMillis-milliTimeStamp;
    if (waitMillis>0) {
      if (debug)
        LOG.info(String.format("waiting %3d msecs",waitMillis));
      try {
        Thread.sleep(waitMillis);
      } catch (InterruptedException e) {
        // ignore
      }
    }
    // shall we "replay" the latest current Image?
    if (this.staticImage && currentImage!=null) {
      Mat frame=currentImage.getFrame().clone();
      currentImage=createNextImage(frame,currentMillis);
    } else {
      Mat frame = new Mat();
      this.capture.read(frame);
      currentImage=createNextImage(frame,currentMillis);
    }
    return currentImage;
  }
  
  /**
   * create the next Image
   * @param frame - the frame to use
   * @param currentMillis - the timestamp to use
   * @return the new next image
   */
  public Image createNextImage(Mat frame, long currentMillis) {
    Image image=null;
    if (!frame.empty()) {
      frameIndex++;
      milliTimeStamp = currentMillis;
      image=new Image(frame,source,frameIndex,milliTimeStamp);
    }
    return image;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
  }

  /**
   * convert me to an observable
   * @return an Image emitting Observable 
   */
  public Observable<Image> toObservable() {
    // Resource creation.
    Func0<VideoCapture> resourceFactory = () -> {
      VideoCapture capture = new VideoCapture();
      capture.open(source);
      return capture;
    };

    // Convert to observable.
    Func1<VideoCapture, Observable<Image>> observableFactory = capture -> Observable
        .<Image> create(subscriber -> handleImage(subscriber));

    // Disposal function.
    Action1<VideoCapture> dispose = VideoCapture::release;

    return Observable.using(resourceFactory, observableFactory, dispose);
  }

  /**
   * handle a single image for the given subscriber
   * @param subscriber
   */
  private void handleImage(Subscriber<? super Image> subscriber) {
    {
      boolean hasNext = true;
      while (hasNext) {
        final Image image = this.fetch();
        final Mat frame=image!=null?image.getFrame():null;
        final Size size=frame!=null?frame.size():new Size(0,0);
        hasNext = image!=null && size.width>0 && size.height>0;
        if (hasNext) {
           if (debug && frameIndex%25==0) {     
             String msg = String.format("->%6d:%4dx%d", frameIndex, size.width,size.height);
             LOG.info(msg);
           }
           subscriber.onNext(image);
        }
      }
      subscriber.onCompleted();
    }
  }
  
}
