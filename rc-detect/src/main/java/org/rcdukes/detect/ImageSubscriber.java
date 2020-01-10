package org.rcdukes.detect;

import org.opencv.core.Mat;

import nl.vaneijndhoven.dukes.common.ErrorHandler;
import rx.Subscriber;

/**
 * a subscriber for OpenCV  images
 * @author wf
 *
 */
public class ImageSubscriber extends Subscriber<Mat> {

  public Throwable error;
  public int cols = 0;
  public int rows=0;
  public int frameIndex=0;
  public boolean completed = false;
  public boolean debug = false;
  public String stackTraceText;

  @Override
  public void onCompleted() {
    completed = true;
  }

  @Override
  public void onError(Throwable th) {
    stackTraceText=ErrorHandler.getStackTraceText(th);
    error = th;
  }

  @Override
  public void onNext(Mat mat) {
    cols = mat.cols();
    rows = mat.rows();
    frameIndex++;
    if (cols==0 || rows==0)
      System.err.println("invalid frame "+frameIndex);
    if (debug && frameIndex%25==0) {
      String msg = String.format("%6d:%4dx%d", frameIndex, cols, rows);
      System.out.println(msg);
    }
  }
};
