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
    String msg=String.format("recording: %dx%d video",testMat.width(),testMat.height());
    System.out.println(msg);
    Size frameSize = new Size(testMat.height(),testMat.width());
    boolean isColor=true;
    VideoRecorder recorder = new VideoRecorder("test",isColor);
    
    recorder.start(25.0, frameSize);
    for (int i = 1; i <= 50; i++) {
      recorder.recordMat(testMat);
    }
    recorder.stop();
    File videoFile=new File(recorder.path);
    assertTrue(videoFile.exists());
  }
}
