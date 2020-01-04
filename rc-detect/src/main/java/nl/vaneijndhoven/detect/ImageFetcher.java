package nl.vaneijndhoven.detect;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * fetcher for Images
 *
 */
public class ImageFetcher {
  // OpenCV video capture
  private VideoCapture capture = new VideoCapture();
  private String source;
  protected int frameIndex;

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
    return ret;
  }

  /**
   * fetch an image Matrix
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
    this.capture.read(frame);
    frameIndex++;
    return !frame.empty() ? frame : null;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
  }

  public Observable<Mat> toObservable() {
    // Resource creation.
    Func0<VideoCapture> resourceFactory = () -> {
      VideoCapture capture = new VideoCapture();
      capture.open(source);
      return capture;
    };

    // Convert to observable.
    Func1<VideoCapture, Observable<Mat>> observableFactory = capture -> Observable
        .<Mat> create(subscription -> {
          boolean hasNext = true;
          while (hasNext) {
            final Mat frame = this.fetch();
            hasNext = frame!=null;
            if (hasNext)
              subscription.onNext(frame);
          }
          subscription.onCompleted();
        });

    // Disposal function.
    Action1<VideoCapture> dispose = VideoCapture::release;

    return Observable.using(resourceFactory, observableFactory, dispose);
  }
}
