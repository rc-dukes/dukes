package com.bitplan.detect;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Mat;

import com.bitplan.opencv.NativeLibrary;

import nl.vaneijndhoven.detect.ImageFetcher;

/**
 * test the ImageFetche observable
 * 
 * @author wf
 *
 */
public class TestImageFetcher {
  boolean debug=true;
  
  @BeforeClass
  public static void setup() throws Exception {
    NativeLibrary.load();
  }

  @Test
  public void testImageFetcher() {
    String testSource = "http://wiki.bitplan.com/videos/full_run.mp4";
    ImageFetcher imageFetcher = new ImageFetcher(testSource);
    Mat mat;
    do {
      mat = imageFetcher.fetch();
    } while (mat != null);
    if (debug) {
      String msg=String.format("%s has %d frames",testSource,imageFetcher.getFrameIndex());
      System.out.println(msg);
    }
    assertEquals(489,imageFetcher.getFrameIndex());
  }

}
