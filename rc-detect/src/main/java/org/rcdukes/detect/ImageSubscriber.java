package org.rcdukes.detect;

import org.opencv.core.Mat;
import org.rcdukes.video.Image;

import nl.vaneijndhoven.dukes.common.ErrorHandler;
import rx.Subscriber;

/**
 * a subscriber for OpenCV  images
 * @author wf
 *
 */
public class ImageSubscriber extends Subscriber<Image> {

  public Throwable error;
  public int cols = 0;
  public int rows=0;
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
  public void onNext(Image image) {
    Mat mat=image.getFrame();
    cols = mat.cols();
    rows = mat.rows();
    if (cols==0 || rows==0)
      System.err.println("invalid frame "+image.getFrameIndex());
    if (debug && image.getFrameIndex()%25==0) {
      String msg = String.format("%6d:%4dx%d", image.getFrameIndex(), cols, rows);
      System.out.println(msg);
    }
  }
};
