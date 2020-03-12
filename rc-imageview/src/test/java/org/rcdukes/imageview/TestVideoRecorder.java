package org.rcdukes.imageview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.opencv.core.Mat;
import org.rcdukes.imageview.DebugImageServer.ImageFormat;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageCollector;
import org.rcdukes.video.ImageCollector.ImageType;
import org.rcdukes.video.ImageUtils;
import org.rcdukes.video.VideoRecorder;
import org.rcdukes.video.VideoRecorders;
import org.rcdukes.video.VideoRecorders.VideoInfo;

import io.vertx.core.json.JsonObject;

/**
 * test the video recording functionality
 * 
 * @author wf
 *
 */
public class TestVideoRecorder extends OpenCVBasedTest {
  public static boolean debug=true;

  @Test
  public void testVideoRecorder() throws IOException {
    DebugImageServer.imageFormat=ImageFormat.jpg;
    ImageCollector imageCollector=new ImageCollector();
    Image testImage=imageCollector.getImage(ImageType.camera,true);
    Mat testMat = testImage.getFrame();

    for (String ext : VideoRecorder.exts) {
      for (String FOURCC : VideoRecorder.FOURCCs) {
        FOURCC = FOURCC.toLowerCase();
        VideoRecorder recorder = new VideoRecorder("test", 25);
        recorder.setExt(ext);
        recorder.FOURCC = FOURCC;
        String msg = String.format(
            "recording: %dx%d %d channel %s video with %s", testMat.width(),
            testMat.height(), testMat.channels(), ext, FOURCC);
        System.out.println(msg);
        for (int i = 1; i <= 50; i++) {
          recorder.recordMat(testMat);
        }
        recorder.stop();
        File videoFile = new File(recorder.getPath());
        assertTrue(videoFile.exists());
        assertTrue(videoFile.length() > 50000);
      }
    }
  }
  
  @Test
  public void testVideoRecorders() throws IOException {
    VideoRecorders vr=new VideoRecorders(10.);
    ImageCollector imageCollector=new ImageCollector();
    String imagePath="images/road.jpg";
    vr.start();
    Mat frame = ImageUtils.fromResource(this.getClass(), imagePath);
    imageCollector.addImage(frame, ImageType.camera);
    vr.recordFrame(imageCollector);
    VideoInfo info = vr.stop();
    if (debug) {
      JsonObject infoJo = JsonObject.mapFrom(info);
      System.out.println(infoJo.encodePrettily());
    }
    assertEquals(10.0,info.fps,0.01);
    assertEquals(-1,info.minFrameIndex.intValue());
    assertEquals(-1,info.maxFrameIndex.intValue());
    assertFalse(info.path.contains(".avi"));
  }
}
