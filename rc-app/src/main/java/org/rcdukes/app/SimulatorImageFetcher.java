package org.rcdukes.app;

import java.io.IOException;

import org.opencv.core.Mat;
import org.rcdukes.error.ErrorHandler;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageUtils;

import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;
import io.vertx.rxjava.core.eventbus.Message;

/**
 * supplies images from the simulator
 * @author wf
 *
 */
public class SimulatorImageFetcher {
  
  private int frameIndex;
  // http://reactivex.io/RxJava/javadoc/rx/subjects/ReplaySubject.html
  ReplaySubject<Image> subject;
  Image Empty=new Image(null,"simulator",-1,0);
  
  public SimulatorImageFetcher() {
    frameIndex=0;
    subject=ReplaySubject.create();
  }
  
  /**
   * receive an image from the simulator
   */
  protected void receiveSimulatorImage(Message<String> message) {
    String imgData = message.body(); // in DataURL format ...
    try {
      Mat frame=ImageUtils.matFromDataUrl(imgData, "jpg");
      long milliTimeStamp = System.currentTimeMillis();
      Image image = new Image(frame, "simulator", frameIndex++,
          milliTimeStamp);
      subject.onNext(image);
    } catch (IOException e) {
      ErrorHandler.getInstance().handle(e);
    }
  }

  /**
   * get my Observable
   * @return my observable
   */
  public Observable<Image> toObservable() {
    return subject;
  }
}
