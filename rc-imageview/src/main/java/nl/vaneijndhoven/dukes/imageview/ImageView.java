package nl.vaneijndhoven.dukes.imageview;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import nl.vaneijndhoven.detect.Detector;
import nl.vaneijndhoven.dukes.common.Config;

import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Imageview verticle (Roscoe)
 *
 */
public class ImageView extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(ImageView.class);

  @Override
  public void start() throws Exception {
    int port = Config.getEnvironment().getInteger(Config.IMAGEVIEW_PORT);
    LOG.info(
        "Starting ImageView Roscoe (lane detection debug image web server on port "
            + port);
    vertx.createHttpServer().requestHandler(this::sendImage).listen(port);
  }

  /**
   * send an image for the given request
   * 
   * @param request
   */
  private void sendImage(HttpServerRequest request) {
    String type = request.getParam("type");
    if (type==null) type="debug";
    byte[] bytes = new byte[] {};
    switch (type) {
    case "edges":
      bytes = Detector.CANNY_IMG;
      break;

    case "birdseye":
      if (Detector.BIRDS_EYE != null) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", Detector.BIRDS_EYE, matOfByte);
        bytes = matOfByte.toArray();
      }
    default:
      if (Detector.MAT != null) {
        MatOfByte matOfByte = new MatOfByte();
        try {
          if (Detector.MAT.width()>0) {
            Imgcodecs.imencode(".png", Detector.MAT, matOfByte);
            bytes = matOfByte.toArray();
          } else {
            String msg=String.format("can't encode %d x %d size image",Detector.MAT.width(),Detector.MAT.height());
            LOG.trace(msg);
          }
        } catch (org.opencv.core.CvException cve) {
          LOG.warn("image encoding failed: "+cve.getMessage());
          bytes=null;
        }
      }
    }

    if (bytes == null) {
      bytes = new byte[] {};
    }
    request.response().putHeader("content-type", "image/png");
    request.response().putHeader("content-length", "" + bytes.length);
    request.response().write(Buffer.buffer().appendBytes(bytes));
  }

}