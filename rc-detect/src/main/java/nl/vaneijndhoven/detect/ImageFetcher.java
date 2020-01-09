package nl.vaneijndhoven.detect;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
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
  public Mat fetch() {
    if (!this.capture.isOpened()) {
      boolean ret = this.open();
      if (!ret) {
        String msg = String.format(
            "Trying to fetch image from unopened VideoCapture and open %s failed",
            source);
        throw new IllegalStateException(msg);
      }
    }
    final Mat frame = new Mat();
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
    this.capture.read(frame);
    frameIndex++;
    milliTimeStamp = currentMillis;
    return !frame.empty() ? frame : null;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
  }

  /**
   * convert me to an observable
   * @return a Mat emitting Observable 
   */
  public Observable<Mat> toObservable() {
    // Resource creation.
    Func0<VideoCapture> resourceFactory = () -> {
      VideoCapture capture = new VideoCapture();
      capture.open(source);
      return capture;
    };

    // Convert to observable.
    Func1<VideoCapture, Observable<Mat>> observableFactory = capture -> Observable
        .<Mat> create(subscriber -> {
          boolean hasNext = true;
          while (hasNext) {
            final Mat frame = this.fetch();
            hasNext = frame!=null && frame.rows()>0 && frame.cols()>0;
            if (hasNext) {
               String msg = String.format("->%6d:%4dx%d", frameIndex, frame.cols(), frame.rows());
               System.out.println(msg);
               subscriber.onNext(frame);
            }
          }
          subscriber.onCompleted();
        });

    // Disposal function.
    Action1<VideoCapture> dispose = VideoCapture::release;

    return Observable.using(resourceFactory, observableFactory, dispose);
  }
}
