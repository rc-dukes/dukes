package org.rcdukes.detect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.rcdukes.opencv.NativeLibrary;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageCollector;
import org.rcdukes.video.ImageCollector.ImageType;

/**
 * test the image Collector
 * @author wf
 *
 */
public class TestImageCollector {
  @BeforeClass
  public static void setup() throws Exception {
    NativeLibrary.load();
  }

  @Test
  public void testImageCollector() {
    ImageCollector imageCollector=new ImageCollector();
    for (ImageType imageType:ImageType.values()) {
      Image testImage = imageCollector.getTestImage(imageType);
      assertNotNull(testImage);
      assertEquals(imageType.name(),testImage.getName());
      assertEquals(0,testImage.getFrameIndex());
      assertTrue(testImage.getMilliTimeStamp()<=System.currentTimeMillis());
    }
  }
}
