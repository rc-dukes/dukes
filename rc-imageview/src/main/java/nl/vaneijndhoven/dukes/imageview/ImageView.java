package nl.vaneijndhoven.dukes.imageview;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import io.vertx.core.Future;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;
import nl.vaneijndhoven.detect.Detector;
import nl.vaneijndhoven.dukes.common.Characters;
import nl.vaneijndhoven.dukes.common.Config;
import nl.vaneijndhoven.dukes.common.DukesVerticle;
import nl.vaneijndhoven.opencv.video.ImageUtils;

/**
 * Imageview verticle (Rosco)
 *
 */
public class ImageView extends DukesVerticle {

  /**
   * construct me with my character information
   */
  public ImageView() {
    super(Characters.ROSCOE);
  }

  protected HttpServer server;
  // @TODO Make configurable
  // the format to be used for image encoding
  public static String ext=".png";
  public static String defaultImage="640px-4_lane_highway_roads_in_India_NH_48_Karnataka_3";

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    super.preStart();
    // @TODO - get config information from config Verticle (or shared data ...)
    int port = Config.getEnvironment().getInteger(Config.IMAGEVIEW_PORT);
    server = vertx.createHttpServer().requestHandler(this::sendImage);
    // Now bind the server:
    server.listen(port, res -> {
      if (res.succeeded()) {
        startFuture.complete();
        super.postStart();
        String msg=String.format("web server on port %d serving debug images in %s format",port,ext);
        LOG.info(msg);
      } else {
        startFuture.fail(res.cause());
      }
    });
  }

  /**
   * send an image for the given request
   * 
   * @param request
   */
  public void sendImage(HttpServerRequest request) {
    String type = request.getParam("type");
    if (type == null)
      type = "debug";
    byte[] bytes =null;
    switch (type) {
    case "edges":
      bytes = Detector.CANNY_IMG;
      break;

    case "birdseye":
      bytes=ImageUtils.mat2ImageBytes(Detector.BIRDS_EYE, ext);
      break;
    default: // debug
      bytes=ImageUtils.mat2ImageBytes(Detector.MAT, ext);
    }
    sendImageBytesOrDefault(request,bytes);
  }

  /**
   * send the given bytes if they are not null or replace with a default image
   * @param request - the request to respond to
   * @param bytes  - the bytes to be send
   */
  private void sendImageBytesOrDefault(HttpServerRequest request,
      byte[] bytes) {
    if (bytes==null) {
      try {
        bytes=IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream(defaultImage+ext));
      } catch (IOException e) {
        LOG.trace(e.getMessage());
      }
    }
    if (bytes!=null)
      this.sendImageBytes(request, bytes);
  }

  /**
   * send the given bytes as an image
   * @param bytes
   */
  public void sendImageBytes(HttpServerRequest request, byte[] bytes) {
    HttpServerResponse response = request.response();
    response.putHeader("content-type", "image/png");
    response.putHeader("content-length", "" + bytes.length);
    Buffer data = Buffer.buffer().appendBytes(bytes);
    response.write(data);
  }
}