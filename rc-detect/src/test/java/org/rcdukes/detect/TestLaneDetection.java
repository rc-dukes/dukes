package org.rcdukes.detect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map.Entry;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Mat;
import org.rcdukes.opencv.NativeLibrary;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageCollector;
import org.rcdukes.video.ImageCollector.ImageType;
import org.rcdukes.video.ImageUtils;

public class TestLaneDetection {
  @BeforeClass
  public static void setup() throws Exception {
    NativeLibrary.load();
  }
  
  public void detect(Image image,String prefix) {
    LaneDetector ld=LaneDetector.getDefault();
    ld.detect(image);
    ImageCollector c = ld.getCollector();
    assertNotNull(c);
    ImageUtils imageUtils=new ImageUtils();
    for (Entry<ImageType, Image> imageEntry:c.getImages().entrySet()) {
      image = imageEntry.getValue();
      if (image.getFrame()!=null)
        imageUtils.writeImage(image.getFrame(),prefix+imageEntry.getKey()+Image.ext);
    }
  }
  
  @Test
  public void testLaneDetection() throws Exception {
    ImageUtils imageUtils=new ImageUtils();
    Mat frame=ImageUtils.read("https://upload.wikimedia.org/wikipedia/commons/thumb/f/f2/4_lane_highway_roads_in_India_NH_48_Karnataka_3.jpg/1280px-4_lane_highway_roads_in_India_NH_48_Karnataka_3.jpg");
    assertNotNull(frame);
    assertEquals(1280,frame.width());
    assertEquals(960,frame.height());
    imageUtils.writeImage(frame,"NH75-India.jpg");
    Image image=new Image(frame,"NH75-India",0,System.currentTimeMillis());
    detect(image,image.getName());
  }
  
  @Test
  public void testLaneDetectionWithImageFetcher() {
    ImageFetcher imageFetcher = TestImageFetcher.getTestImageFetcher();
    imageFetcher.open();
    Image image = imageFetcher.fetch();
    Mat frame=image.getFrame();
    assertEquals(1,image.getFrameIndex());
    assertEquals(768,frame.width());
    assertEquals(576,frame.height());
    assertEquals(3,frame.channels());
    detect(image,"Utrecht");
    imageFetcher.close();
  }
  
  
}
