package org.rcdukes.imageview;

import static org.asynchttpclient.Dsl.asyncHttpClient;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.Future;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.asynchttpclient.handler.BodyDeferringAsyncHandler;
import org.asynchttpclient.handler.BodyDeferringAsyncHandler.BodyDeferringInputStream;

/**
 * handler for MJPeg Streams
 * @author wf
 *
 */
public class MJpegHandler {

  AsyncHttpClient asyncHttpClient;
  private PipedInputStream pipedInputStream;
  private PipedOutputStream pipedOutputStream;
  private BodyDeferringAsyncHandler outputHandler;
  BodyDeferringInputStream inputStream;
  
  public BodyDeferringInputStream getInputStream() {
    return inputStream;
  }

  /**
   * get an mjpeg stream from the given url
   * 
   * @param url
   * @return - the MJPeg Stream
   * @throws Exception
   */
  public MJpegHandler(String url) throws Exception {
    // https://stackoverflow.com/a/50402629/1497139
    asyncHttpClient = asyncHttpClient();
    asyncHttpClient.prepareGet(url);
    pipedInputStream = new PipedInputStream();
    pipedOutputStream = new PipedOutputStream(
        pipedInputStream);
    outputHandler = new BodyDeferringAsyncHandler(
        pipedOutputStream);
    Future<Response> futureResponse = asyncHttpClient.prepareGet(url)
        .execute(outputHandler);
    Response response = outputHandler.getResponse();
    if (response.getStatusCode() == 200) {
      inputStream=new BodyDeferringAsyncHandler.BodyDeferringInputStream(
          futureResponse, outputHandler, pipedInputStream);
    } 
  }

  public MJpegHandler() {
  }

  /**
   * open me with the given bufferSize
   * 
   * @param url
   * @return
   * @throws Exception
   */
  public MJpegDecoder open(int bufferSize) throws Exception {
    MJpegDecoder mjpegDecoder = new MJpegDecoder(this);
    mjpegDecoder.open(bufferSize);
    return mjpegDecoder;
  }
  
  /**
   * close this handler
   * @throws IOException
   */
  public void close() throws IOException {
     this.asyncHttpClient.close();
  }

}
