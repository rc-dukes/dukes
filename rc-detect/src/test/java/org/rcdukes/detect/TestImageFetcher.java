package org.rcdukes.detect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.rcdukes.video.Image;

import io.reactivex.Observable;

/**
 * test the ImageFetcher observable
 * 
 * @author wf
 *
 */
public class TestImageFetcher extends BaseDetectTest {
 

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
    ImageObserver imageObserver = new ImageObserver();
    imageObserver.debug=debug;
    imageObservable.subscribe(imageObserver);
    assertNull(imageObserver.error);
    assertTrue(imageObserver.completed);
    assertEquals(768,imageObserver.cols);
    assertEquals(576,imageObserver.rows);
  }
  
  @Test
  public void testImageFetcherObservableSample() {
    ImageFetcher imageFetcher = getTestImageFetcher();
    Observable<Image> imageObservable = imageFetcher.toObservable();
    ImageObserver imageObserver = new ImageObserver();
    imageObserver.debug=debug;
    // sample at 25 fps
    imageObservable.sample(40, TimeUnit.MILLISECONDS).subscribe(imageObserver);
    assertNull(imageObserver.error);
    assertTrue(imageObserver.completed);
    assertEquals(768,imageObserver.cols);
    assertEquals(576,imageObserver.rows);
  }
  
  @Test
  public void testStaticImage() {
    String testImageUrl="https://upload.wikimedia.org/wikipedia/commons/thumb/f/f2/4_lane_highway_roads_in_India_NH_48_Karnataka_3.jpg/1280px-4_lane_highway_roads_in_India_NH_48_Karnataka_3.jpg";
    ImageFetcher imageFetcher=new ImageFetcher(testImageUrl);
    assertTrue(imageFetcher.open());
    assertTrue(imageFetcher.isStaticImage());
    for (int i=0;i<=10;i++) {
      Image image=imageFetcher.fetch();
      assertNotNull(image);
    }
    
  }

}
