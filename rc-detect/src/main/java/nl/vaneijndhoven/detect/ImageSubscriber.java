package nl.vaneijndhoven.detect;

import org.opencv.core.Mat;

import rx.Subscriber;

public class ImageSubscriber extends Subscriber<Mat> {

  public Throwable error;
  public int cols = 0;
  public int rows=0;
  public int frameIndex=0;
  public boolean completed = false;
  public boolean debug = false;

  @Override
  public void onCompleted() {
    completed = true;
  }

  @Override
  public void onError(Throwable e) {
    error = e;
  }

  @Override
  public void onNext(Mat mat) {
    cols = mat.cols();
    rows = mat.rows();
    frameIndex++;
    if (cols==0 || rows==0)
      System.err.println("invalid frame "+frameIndex);
    if (debug) {
      String msg = String.format("%6d:%4dx%d", frameIndex, cols, rows);
      System.out.println(msg);
    }
  }
};
