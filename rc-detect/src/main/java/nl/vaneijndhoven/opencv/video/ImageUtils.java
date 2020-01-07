package nl.vaneijndhoven.opencv.video;

import java.io.ByteArrayInputStream;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.image.Image;

/**
 * Image Utilities
 * 
 * @author wf
 *
 */
public class ImageUtils {
  protected static final Logger LOG = LoggerFactory.getLogger(ImageUtils.class);

  /**
   * convert an open CV matrix to an Image this function will log messages on
   * failure an return null in case of such a failure
   * 
   * @param frame
   *          - the open cv matrix
   * @param ext
   *          - the format to be used e.g ".png", ".jpg"
   * @return the converted image
   */
  public static Image mat2Image(Mat frame, String ext) {
    byte[] imageBytes = ImageUtils.mat2ImageBytes(frame, ext);
    Image image = ImageUtils.imageBytes2Image(imageBytes);
    return image;
  }
  
  /**
   * convert imageBytes to an image
   * @param imageBytes
   * @return image or null if imageBytes were already null
   */
  public static Image imageBytes2Image(byte[] imageBytes) {
    Image image = null;
    if (imageBytes != null)
      image = new Image(new ByteArrayInputStream(imageBytes));
    return image;
  }

  /**
   * convert the given Open CV matrix to a byte array this function will log
   * issues on error and return null in case of such a failure
   * 
   * @param frame
   * @param ext
   *          -the format to be used e.g. .png, .jpg
   * @return - the byte array - may be null if there was an error
   */
  public static byte[] mat2ImageBytes(Mat frame, String ext) {
    byte[] bytes = null;
    try {
      if (frame == null) {
        String msg=String.format("can't encode null frame to %s",ext);
        LOG.trace(msg);
      } else {
        if (frame.width() > 0) {
          MatOfByte buffer = new MatOfByte();
          Imgcodecs.imencode(ext, frame, buffer);
          bytes = buffer.toArray();
        } else {
          String msg = String.format("can't encode %d x %d size image to %s",
              frame.width(), frame.height(),ext);
          LOG.trace(msg);
        }
      }
    } catch (org.opencv.core.CvException cve) {
      String msg=String.format("image encoding to %s failed: %s",ext,cve.getMessage());
      LOG.warn(msg);
    }
    return bytes;
  }

  /**
   * release the given images
   * 
   * @param images
   */
  public static void releaseImages(Mat... images) {
    for (Mat image : images) {
      image.release();
    }
  }

}
