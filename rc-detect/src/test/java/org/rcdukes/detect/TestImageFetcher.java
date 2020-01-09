package org.rcdukes.detect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Mat;
import org.rcdukes.detect.ImageFetcher;
import org.rcdukes.detect.ImageSubscriber;

import com.bitplan.opencv.NativeLibrary;

import rx.Observable;

/**
 * test the ImageFetcher observable
 * 
 * @author wf
 *
 */
public class TestImageFetcher {
  boolean debug = false;

  @BeforeClass
  public static void setup() throws Exception {
    NativeLibrary.load();
  }

  public static String testSource = "http://wiki.bitplan.com/videos/full_run.mp4";

  /**
   * get an ImageFetcher based on a test video
   * 
   * @return - the imageFetcher
   */
  public static ImageFetcher getTestImageFetcher() {
    ImageFetcher imageFetcher = new ImageFetcher(testSource);
    return imageFetcher;
  }

  @Test
  public void testImageFetcher() {
    ImageFetcher imageFetcher = getTestImageFetcher();
    Mat mat;
    do {
      mat = imageFetcher.fetch();
    } while (mat != null);
    if (debug) {
      String msg = String.format("%s has %d frames", testSource,
          imageFetcher.getFrameIndex());
      System.out.println(msg);
    }
    assertEquals(489, imageFetcher.getFrameIndex());
  }
 
  @Test
  public void testImageFetcherObservable() {
    ImageFetcher imageFetcher = getTestImageFetcher();
    Observable<Mat> imageObservable = imageFetcher.toObservable();
    ImageSubscriber imageUser = new ImageSubscriber();
    imageUser.debug=debug;
    imageObservable.subscribe(imageUser);
    assertNull(imageUser.error);
    assertTrue(imageUser.completed);
    assertEquals(768,imageUser.cols);
    assertEquals(576,imageUser.rows);
    assertEquals(488,imageUser.frameIndex);
  }
  
  @Test
  public void testImageFetcherObservableSample() {
    ImageFetcher imageFetcher = getTestImageFetcher();
    Observable<Mat> imageObservable = imageFetcher.toObservable();
    ImageSubscriber imageUser = new ImageSubscriber();
    imageUser.debug=debug;
    // sample at 25 fps
    imageObservable.sample(40, TimeUnit.MILLISECONDS).subscribe(imageUser);
    assertNull(imageUser.error);
    assertTrue(imageUser.completed);
    assertEquals(768,imageUser.cols);
    assertEquals(576,imageUser.rows);
    assertTrue(imageUser.frameIndex<488);
  }

}
