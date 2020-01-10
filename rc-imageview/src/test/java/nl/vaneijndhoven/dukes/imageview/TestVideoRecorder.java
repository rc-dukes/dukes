package nl.vaneijndhoven.dukes.imageview;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Mat;
import org.rcdukes.video.ImageUtils;

import com.bitplan.opencv.NativeLibrary;

import nl.vaneijndhoven.dukes.imageview.DebugImageServer.ImageFormat;

/**
 * test the video recording functionality
 * 
 * @author wf
 *
 */
public class TestVideoRecorder {
  @BeforeClass
  public static void setup() throws Exception {
    //redirectStdErr();
    NativeLibrary.load();
  }
  /*
  static PrintStream err;
  static ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  // static PrintStream out;

  public static void redirectStdErr() {
    err = System.err;
    System.setErr(new PrintStream(errContent));
    System.err.println("Catching messages on System.err ...");
  }

  /**
   * restore std err and return the catched result
   * 
   * @return the string content of the grabbed stderr
   * @throws IOException
   /
  public static String restoreStdErr() throws IOException {
    errContent.flush();
    String stderr = new String(errContent.toByteArray(), "utf-8");
    // restore original error handling
    System.setErr(err);
    return stderr;
  }*/

  @Test
  public void testVideoRecorder() throws IOException {
    DebugImageServer.imageFormat=ImageFormat.jpg;
    byte[] testImage = DebugImageServer.testImage();
    Mat testMat = ImageUtils.imageBytes2Mat(testImage);

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
        File videoFile = new File(recorder.path);
        assertTrue(videoFile.exists());
        assertTrue(videoFile.length() > 50000);
      }
    }

    //String stderr = restoreStdErr();
    // System.err.println(stderr);
  }
}
