package org.rcdukes.detect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Mat;
import org.rcdukes.opencv.NativeLibrary;
import org.rcdukes.video.Image;

import rx.Observable;

/**
 * test the ImageFetcher observable
 * 
 * @author wf
 *
 */
public class TestImageFetcher {
  boolean debug = true;

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
    imageFetcher.debug=debug;
    Image image;
    do {
       image= imageFetcher.fetch();
    } while (image != null);
    if (debug) {
      String msg = String.format("%s has %d frames", testSource,
          imageFetcher.getFrameIndex());
      System.out.println(msg);
    }
    assertEquals(488, imageFetcher.getFrameIndex());
  }
 
  @Test
  public void testImageFetcherObservable() {
    ImageFetcher imageFetcher = getTestImageFetcher();
    Observable<Image> imageObservable = imageFetcher.toObservable();
    ImageSubscriber imageUser = new ImageSubscriber();
    imageUser.debug=debug;
    imageObservable.subscribe(imageUser);
    assertNull(imageUser.error);
    assertTrue(imageUser.completed);
    assertEquals(768,imageUser.cols);
    assertEquals(576,imageUser.rows);
  }
  
  @Test
  public void testImageFetcherObservableSample() {
    ImageFetcher imageFetcher = getTestImageFetcher();
    Observable<Image> imageObservable = imageFetcher.toObservable();
    ImageSubscriber imageUser = new ImageSubscriber();
    imageUser.debug=debug;
    // sample at 25 fps
    imageObservable.sample(40, TimeUnit.MILLISECONDS).subscribe(imageUser);
    assertNull(imageUser.error);
    assertTrue(imageUser.completed);
    assertEquals(768,imageUser.cols);
    assertEquals(576,imageUser.rows);
  }

}
