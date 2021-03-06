package org.rcdukes.detect;

import java.util.Locale;

import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.rcdukes.video.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * fetcher for Images - will block at the given FPS rate
 *
 */
public class ImageFetcher {
  protected static final Logger LOG = LoggerFactory
      .getLogger(ImageFetcher.class);

  public boolean debug = false;
  public static double DEFAULT_FPS = 25;
  // OpenCV video capture
  private VideoCapture capture = new VideoCapture();
  private String source;
  protected int frameIndex;
  protected int failureCount = 0;
  protected int maxFailureCount = 3;
  protected double fps = DEFAULT_FPS; // frames per second
  private long milliTimeStamp;
  private boolean staticImage;
  private boolean closed = false;
  private boolean hasNext=false;

  private Image currentImage = null;

  private Mat frame;
  private Size size;
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
   * @param staticImage
   *          the staticImage to set
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
    String ext = FilenameUtils.getExtension(source).toLowerCase();
    setStaticImage(false);
    switch (ext) {
    case "jpg":
    case "png":
    case "jpeg":
      setStaticImage(true);
    }
    frameIndex = 0;
    failureCount = 0;
    milliTimeStamp = System.nanoTime() / 1000000;
    hasNext=true;
    return ret;
  }

  public void close() {
    if (!isClosed()) {
      this.capture.release();
    }
    closed=true;
  }

  /**
   * wait for the next Frame
   * @return the current time in milliseconds
   */
  public long waitForNextFrame() {
    long currentMillis = System.nanoTime() / 1000000;
    int waitMillis = (int) Math.round(1000 / fps);
    waitMillis -= currentMillis - milliTimeStamp;
    if (waitMillis > 0) {
      if (debug)
        LOG.info(String.format(Locale.ENGLISH, "waiting %3d msecs for %.1f fps",
            waitMillis, getFps()));
      try {
        Thread.sleep(waitMillis);
      } catch (InterruptedException e) {
        // ignore
      }
    }
    return currentMillis;
  }
  
  /**
   * fetch an image Matrix - will block to make sure Mat is emitted at the
   * frames per second rate configured
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
    long currentMillis=waitForNextFrame();
  
    // shall we "replay" the latest current Image?
    if (this.staticImage && currentImage != null) {
      Mat frame = currentImage.getFrame().clone();
      currentImage = createNextImage(frame, currentMillis);
    } else {
      Mat frame = new Mat();
      this.capture.read(frame);
      if (!frame.empty()) {
        currentImage = createNextImage(frame, currentMillis);
        failureCount = 0;
      } else {
        failureCount++;
        if (maxFailureCount < getFps()
            && failureCount % Math.round(getFps() * 2) == 0) {
          String msg = String.format("%d read failures for %s", failureCount,
              source);
          LOG.info(msg);
        }
        if (failureCount > maxFailureCount) {
          hasNext=false;
          return null;
        }
      }
    }
    milliTimeStamp = currentMillis;
    frame = currentImage != null ? currentImage.getFrame() : null;
    size = frame != null ? frame.size() : new Size(0, 0);
    hasNext = currentImage != null && size.width > 0 && size.height > 0;
    return currentImage;
  }

  /**
   * create the next Image
   * 
   * @param frame
   *          - the frame to use
   * @param currentMillis
   *          - the timestamp to use
   * @return the new next image
   */
  public Image createNextImage(Mat frame, long currentMillis) {
    frameIndex++;
    milliTimeStamp = currentMillis;
    Image image = new Image(frame, source, frameIndex, milliTimeStamp);
    return image;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
  }

  /**
   * convert me to an observable
   * 
   * @return an Image emitting Observable
   */
  public Observable<Image> toObservable() {
    Observable<Image> observable = Observable
        .create(new ObservableOnSubscribe<Image>() {
          @Override
          public void subscribe(ObservableEmitter<Image> observableEmitter)
              throws Exception {
            hasNext=true;
            while (hasNext && !isClosed()) {
              final Image image = ImageFetcher.this.fetch();
              if (hasNext) {
                if (debug && frameIndex % Math.round(getFps()) == 0) {
                  String msg = String.format("->%6d:%4.0fx%4.0f", frameIndex,
                      size.width, size.height);
                  LOG.info(msg);
                }
                observableEmitter.onNext(image);
              }
            }
            observableEmitter.onComplete();
          }
        });
    return observable;
  }

  public boolean hasNext() {
    return hasNext;
  }

  /**
   * @return the closed
   */
  public boolean isClosed() {
    return closed;
  }

}
