package org.rcdukes.imageview;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Mat;
import org.rcdukes.imageview.DebugImageServer.ImageFormat;
import org.rcdukes.opencv.NativeLibrary;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageCollector;
import org.rcdukes.video.ImageCollector.ImageType;

/**
 * test the video recording functionality
 * 
 * @author wf
 *
 */
public class TestVideoRecorder extends OpenCVBasedTest {
  

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
        recorder.ext = ext;
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
}
