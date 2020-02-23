package org.rcdukes.detect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.opencv.core.Mat;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageCollector;
import org.rcdukes.video.ImageCollector.ImageType;
import org.rcdukes.video.ImageUtils;

/**
 * test the lane detection
 * @author wf
 *
 */
public class TestLaneDetection extends BaseDetectTest {
 
  public void detect(Image image,String prefix) {
    LaneDetector ld=LaneDetector.getDefault();
    ld.detect(image);
    ImageCollector c = ld.getCollector();
    assertNotNull(c);
    ImageUtils imageUtils=new ImageUtils();
    for (ImageType imageType:ImageType.values()) {
      Image cimage=c.getImage(imageType, true);
      if (cimage.getFrame()!=null)
        imageUtils.writeImage(cimage.getFrame(),prefix+cimage.getName()+Image.ext);
    }
  }
  
  @Test
  public void testLaneDetection() throws Exception {
    ImageUtils imageUtils=new ImageUtils();
    Mat frame=super.getTestImage();
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
