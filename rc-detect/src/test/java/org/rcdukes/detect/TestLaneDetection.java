package org.rcdukes.detect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map.Entry;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Mat;
import org.rcdukes.opencv.NativeLibrary;
import org.rcdukes.video.ImageCollector;
import org.rcdukes.video.ImageCollector.Image;
import org.rcdukes.video.ImageUtils;

public class TestLaneDetection {
  @BeforeClass
  public static void setup() throws Exception {
    NativeLibrary.load();
  }
  
  @Test
  public void testLaneDetection() {
    ImageFetcher imageFetcher = TestImageFetcher.getTestImageFetcher();
    imageFetcher.open();
    Mat frame = imageFetcher.fetch();
    assertEquals(768,frame.width());
    assertEquals(576,frame.height());
    assertEquals(3,frame.channels());
    LaneDetector ld=LaneDetector.getDefault();
    ld.detect(frame);
    ImageCollector c = ld.getCollector();
    assertNotNull(c);
    assertNotNull(c.edges());
    assertNotNull(c.lines());
    assertNotNull(c.originalFrame());
    ImageUtils imageUtils=new ImageUtils();
    for (Entry<String, Image> imageEntry:c.getImages().entrySet()) {
      Image image = imageEntry.getValue();
      if (image.getFrame()!=null)
        imageUtils.writeImage(image.getFrame(),imageEntry.getKey()+Image.ext);
    }
    imageFetcher.close();
  }
}
