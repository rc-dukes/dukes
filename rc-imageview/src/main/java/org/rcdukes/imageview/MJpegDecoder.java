package org.rcdukes.imageview;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.asynchttpclient.handler.BodyDeferringAsyncHandler.BodyDeferringInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.Subscriber;
import rx.observables.StringObservable;
import rx.schedulers.Schedulers;

/**
 * reactive MJPegDecoder
 * 
 * @author wf
 *
 */
public class MJpegDecoder extends Subscriber<byte[]> {
  protected static final Logger LOG = LoggerFactory
      .getLogger(MJpegDecoder.class);

  int prev = 0;
  int cur = 0;
  private ByteArrayOutputStream jpgOut;
  private byte[] curFrame;
  public static boolean debug = false;
  private int bufferIndex = 0;
  private int frameIndex = 0;
  FileOutputStream fos;

  private int bufferSize;

  private Observable<byte[]> mjpegSubscription;

  private MJpegHandler mjpegHandler;

  /**
   * open the decoder for the given stream
   * 
   * @param mJpegHandler
   * @param bufferSize
   */
  public MJpegDecoder(MJpegHandler mJpegHandler) {
    this.mjpegHandler = mJpegHandler;
    this.curFrame = new byte[0];
    if (debug) {
      try {
        fos = new FileOutputStream("/tmp/decoder.mjpg");
      } catch (FileNotFoundException e) {
        handle(e);
      }
    }
  }

  @Override
  public void onCompleted() {
    try {
      this.mjpegHandler.close();
      if (fos != null) {
        fos.close();
      }
      fos = null;
    } catch (IOException e) {
      onError(e);
    }
  }

  @Override
  public void onError(Throwable e) {
    handle(e);
  }

  @Override
  public void onNext(byte[] buffer) {
    if (debug) {
      String msg = String.format("buffer %6d available %9d kB read",
          ++bufferIndex, bufferIndex * bufferSize / 1024);
      LOG.info(msg);
      try {
        fos.write(curFrame);
        fos.flush();
      } catch (IOException e) {
        handle(e);
      }

    }
    // loop over all bytes in the buffer
    for (int cur : buffer) {
      // Content-Type: multipart/x-mixed-replace; boundary=
      // will have -- we could detect it here
      if (debug) {
        if (prev == 0x2D && cur == 0x2D) {
          LOG.info("boundary detected");
        }
      }
      // check for JPEG start bytes
      if (prev == 0xFFFFFFFF && cur == 0xFFFFFFD8) {
        if (debug) {
          String msg = String.format("frame %6d started", frameIndex + 1);
          LOG.info(msg);
        }
        jpgOut = new ByteArrayOutputStream(bufferSize);
        // first byte needs to be written to output
        jpgOut.write((byte) prev);
      }
      // if within the frame write all bytes
      if (jpgOut != null) {
        jpgOut.write((byte) cur);
        // check for JPEG end bytes
        // if found the frame is finished
        if (prev == 0xFFFFFFFF && cur == 0xFFFFFFD9) {
          // create the byte array of the current jpeg frame
          curFrame = jpgOut.toByteArray();
          try {
            jpgOut.close();
            jpgOut = null;
          } catch (IOException e) {
            onError(e);
          }

          if (debug) {
            String msg = String.format("frame %6d available", ++frameIndex);
            LOG.info(msg);
          }

          // emit the current frame
        }
      }
      prev = cur;
    }
  }

  private void handle(Throwable th) {
    LOG.error(th.getMessage());
    if (debug)
      th.printStackTrace();
  }

  /**
   * open me with the given bufferSize
   * 
   * @param bufferSize
   *          e.g. 64 KByte Buffer - 10.5 msecs/100 FPS at 1920x1080
   *          1000/(1920*1080*3/1024/64)
   */
  public void open(int bufferSize) {
    this.bufferSize = bufferSize;
    BodyDeferringInputStream inputStream = this.mjpegHandler.getInputStream();
    if (inputStream != null) {
      mjpegSubscription = StringObservable.from(inputStream, bufferSize)
          .subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread());
      mjpegSubscription.subscribe(this);
    }
  }

  /**
   * close me
   */
  public void close() {
    this.unsubscribe();
    this.onCompleted();
  }

}