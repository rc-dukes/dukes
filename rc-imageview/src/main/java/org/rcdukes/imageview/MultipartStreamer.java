package org.rcdukes.imageview;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageUtils;
import org.rcdukes.video.ImageCollector.ImageType;
import org.rcdukes.video.ImageUtils.CVColor;

import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpServerResponse;

/**
 * Streamer for multipart images
 * 
 * @author wf
 *
 */
public class MultipartStreamer implements Runnable {
  /**
   * 
   */
  private DebugImageServer debugImageServer;
  String crlf = "\r\n";
  boolean running = false;
  private HttpServerResponse response;
  private ImageType imageType;
  String boundary = "frame";
  private Buffer currentData;

  /**
   * create a MultipartStream for the given imageType
   * 
   * @param response
   * @param imageType
   * @param debugImageServer TODO
   */
  public MultipartStreamer(DebugImageServer debugImageServer, HttpServerResponse response, ImageType imageType) {
    this.debugImageServer = debugImageServer;
    this.response = response;
    this.imageType = imageType;
  }

  /**
   * add the info from the image to the given frame
   * 
   * @param frame
   *          - the openCV frame
   * @param image
   *          - the rcdukes image
   */
  public void addImageInfo(Mat frame, Image image) {
    int fontFace = Core.FONT_HERSHEY_SIMPLEX;
    int fontScale = 1;
    Scalar color = CVColor.dodgerblue;
    String text = String.format("%5d", image.getFrameIndex());
    Point pos = new Point(frame.width() - 100, 25);
    Imgproc.putText(frame, text, pos, fontFace, fontScale, color);
  }

  /**
   * serve the next image
   */
  public void streamNextImage() {
    Image image=this.debugImageServer.getNextImage(imageType);
    if (image != null) {
      Mat frame = image.getFrame().clone();
      addImageInfo(frame, image);
      byte[] bytes = ImageUtils.mat2ImageBytes(frame,
          DebugImageServer.exts[DebugImageServer.imageFormat.ordinal()]);
      currentData = Buffer.buffer().appendBytes(bytes);
      if (currentData!=null) {
        this.writeMultiPartFrame(currentData);
        this.debugImageServer.recordFrame(frame,imageType);
      }
    }
    // 1 image per second
    // @TODO change to emitter to make configurable
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      // ...
    }
  }
  
  /**
   * write a multipart frame
   * @param imageBytes
   */
  public void writeMultiPartFrame(Buffer imageBytes) {
    String contentType = DebugImageServer.contentTypes[DebugImageServer.imageFormat.ordinal()];
    response.write("--" + boundary + crlf);
    response.write("Content-Type: " + contentType + crlf);
    response.write("Content-Length: " + imageBytes.length() + crlf);
    response.write(crlf);
    response.write(imageBytes);
  }

  public void preamble() {
    // initiate a multipart response
    response.setChunked(true); // ! important

    String contentType = "multipart/x-mixed-replace; boundary=" + boundary;
    // switch of caching
    // https://stackoverflow.com/a/2068407/1497139
    response.putHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
    response.putHeader("Pragma", "no-cache"); // HTTP 1.0.
    response.putHeader("Expires", "0"); // Proxies.
    response.putHeader("content-type", contentType);
  }

  @Override
  public void run() {
    int streamIndex = ++DebugImageServer.streamCounter;
    System.out.println("Starting image stream " + streamIndex);
    preamble();
    running = true;
    while (running) {
      running=!response.ended() || response.closed();
      if (running)
        this.streamNextImage();
    }
    System.out.println("Image stream " + streamIndex + " ended or closed");
  }
}