package nl.vaneijndhoven.opencv.video;

import static nl.vaneijndhoven.opencv.mapper.PointMapper.toPoint;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Objects;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.image.Image;
import nl.vaneijndhoven.dukes.geometry.Line;

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
   * 
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
   * convert image bytes to Mat
   * @param bytes
   * @return - the Mat
   */
  public static Mat imageBytes2Mat(byte[] bytes) {
    // https://stackoverflow.com/a/33930741/1497139
    Mat mat=null;
    if (bytes!=null)
      mat = Imgcodecs.imdecode(new MatOfByte(bytes),
        Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
    return mat;
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
        String msg = String.format("can't encode null frame to %s", ext);
        LOG.trace(msg);
      } else {
        if (frame.width() > 0) {
          MatOfByte buffer = new MatOfByte();
          Imgcodecs.imencode(ext, frame, buffer);
          bytes = buffer.toArray();
        } else {
          String msg = String.format("can't encode %d x %d size image to %s",
              frame.width(), frame.height(), ext);
          LOG.trace(msg);
        }
      }
    } catch (org.opencv.core.CvException cve) {
      String msg = String.format("image encoding to %s failed: %s", ext,
          cve.getMessage());
      LOG.warn(msg);
    }
    return bytes;
  }
  
  String path="/tmp/";
  String filePrefix="dukes";
  
  public ImageUtils() {
  }

  /**
   * prepare writing images 
   * @param path
   * @param filePrefix
   */
  public ImageUtils(String path,String filePrefix) {
    this.path=path;
    this.filePrefix=filePrefix;
  }
 
  public void writeImage(Mat img, String name) {
    String fileName = filePrefix.replace(".", "-")+ name;
    Imgcodecs.imwrite(path + fileName, img);
  }
  
  public void writeImageWithLines(Mat img, Collection<Line> lines, String name, Scalar color) {
    String fileName = filePrefix.replace(".", "-" + name + ".");
    Mat output = new Mat();
    img.copyTo(output);
    lines.stream().filter(Objects::nonNull).forEach(line -> Imgproc.line(output, toPoint(line.getPoint1()), toPoint(line.getPoint2()), color, 4));
    Imgcodecs.imwrite(path + fileName, output);
    output.release();
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
