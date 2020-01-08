package nl.vaneijndhoven.dukes.imageview;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import com.bitplan.opencv.NativeLibrary;

import nl.vaneijndhoven.opencv.video.ImageUtils;

public class TestVideoRecorder {
  @BeforeClass
  public static void setup() throws Exception {
    NativeLibrary.load();
  }

  @Test
  public void testVideoRecorder() {
    Size frameSize = new Size(640, 480);
    VideoRecorder recorder = new VideoRecorder("test");
    byte[] testImage = DebugImageServer.testImage();
    Mat testMat = ImageUtils.imageBytes2Mat(testImage);
    recorder.start(25.0, frameSize);
    for (int i = 0; i < 50; i++) {
      recorder.recordMat(testMat);
    }
    recorder.stop();
    File videoFile=new File(recorder.path);
    assertTrue(videoFile.exists());
  }
}
