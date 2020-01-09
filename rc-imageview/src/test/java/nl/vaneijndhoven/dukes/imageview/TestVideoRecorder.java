package nl.vaneijndhoven.dukes.imageview;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import com.bitplan.opencv.NativeLibrary;

import nl.vaneijndhoven.opencv.video.ImageUtils;

/**
 * test the video recording functionality
 * 
 * @author wf
 *
 */
public class TestVideoRecorder {
  @BeforeClass
  public static void setup() throws Exception {
    NativeLibrary.load();
  }

  @Test
  public void testVideoRecorder() {
    byte[] testImage = DebugImageServer.testImage();
    Mat testMat = ImageUtils.imageBytes2Mat(testImage);
    boolean isColor = true;
    for (String ext : VideoRecorder.exts) {
      for (String FOURCC : VideoRecorder.FOURCCs) {
        Size frameSize = new Size(testMat.width(), testMat.height());
        VideoRecorder recorder = new VideoRecorder("test", isColor);
        recorder.ext=ext;
        recorder.FOURCC=FOURCC;
        String msg = String.format("recording: %dx%d %s video with %s",
            testMat.width(), testMat.height(), ext, FOURCC);
        System.out.println(msg);

        recorder.start(25.0, frameSize);
        for (int i = 1; i <= 50; i++) {
          recorder.recordMat(testMat);
        }
        recorder.stop();
        File videoFile = new File(recorder.path);
        assertTrue(videoFile.exists());
      }
    }
  }
}
