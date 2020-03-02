package org.rcdukes.video;

import static org.rcdukes.video.PointMapper.toPoint;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.rcdukes.common.Environment;
import org.rcdukes.error.ErrorHandler;
import org.rcdukes.geometry.Line;
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
  public static boolean debug=false;
  
  /**
   * OpenCV BGR color scheme
   * @author wf
   */
  public static class CVColor {
    public static final Scalar red=new Scalar(0, 0, 255);
    public static final Scalar lightgreen=new Scalar(10,250,20);
    public static final Scalar green=new Scalar(0, 255, 0);
    public static final Scalar dodgerblue=new Scalar(255, 128, 0);
    public static final Scalar cyan=new Scalar(255, 255, 0);
    public static final Scalar yellow=new Scalar(0, 255, 255);
    public static final Scalar indigo=new Scalar(130,0,75);
  }
  
  /**
   * read a buffered Image from the given data Url
   * @param imgData
   * @return a buffered Image
   * @throws IOException
   */
  public static BufferedImage readFromDataUrl(String imgData) throws IOException {
    // https://stackoverflow.com/a/34424596/1497139
    imgData=imgData.substring(imgData.indexOf(",") + 1);
    // FIXME - read from data url;
    byte[] imageEncodedBytes = DatatypeConverter.parseBase64Binary(imgData);
    BufferedImage image=ImageIO.read(new ByteArrayInputStream(imageEncodedBytes));
    return image;
  }
  
  /**
   * get a Mat from the given DataUrl
   * @param imgData
   * @param ext - the image extension to use
   * @return a Mat
   * @throws IOException
   */
  public static Mat matFromDataUrl(String imgData, String ext) throws IOException {
    BufferedImage image = ImageUtils.readFromDataUrl(imgData);
    byte[] imageBytes = ImageUtils.bufferedImage2ImageBytes(image, ext);
    Mat imageMat=ImageUtils.imageBytes2Mat(imageBytes);
    return imageMat;
  }
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
   * get a Mat from the given imagePath resource
   * @param clazz
   * @param imagePath
   * @return the Mat
   * @throws IOException
   */
  public static Mat fromResource(Class<?> clazz,String imagePath) throws IOException {
    byte[] testImageBytes = IOUtils.toByteArray(clazz
        .getClassLoader().getResourceAsStream(imagePath));
    Mat frame = ImageUtils.imageBytes2Mat(testImageBytes);
    return frame;
  }
  
  /**
   * convert the given buffered Image to a byte array
   * @param image
   * @return - the byte array
   */
  public static byte[] bufferedImage2ImageBytes(BufferedImage image, String ext) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ext=ext.replace(".", "");
    try {
      ImageIO.write(image, ext, baos);
    } catch (IOException e) {
      ErrorHandler.getInstance().handle(e);
    }
    byte[] bytes = baos.toByteArray();
    return bytes;
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
        if (debug) {
          String msg = String.format("can't encode null frame to %s", ext);
          LOG.trace(msg);
        }
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
  
  String path=Environment.dukesHome+"media";
  String filePrefix="dukes.";
  
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
 
  /**
   * write an Image
   * @param img
   * @param name
   */
  public void writeImage(Mat img, String name) {
    String fileName = filePrefix.replace(".", "-")+ name;
    writeImageToFilepath(img,path +"/"+fileName);
  }
  
  /**
   * write the given image to the given filepath
   * @param img
   * @param filepath
   */
  public static void writeImageToFilepath(Mat img,String filepath) {  
    Imgcodecs.imwrite(filepath, img);
  }
  
  /**
   * write an image with the given lines
   * @param img
   * @param lines
   * @param name
   * @param color
   */
  public void writeImageWithLines(Mat img, Collection<Line> lines, String name, Scalar color) {
    Mat output = new Mat();
    img.copyTo(output);
    drawLinesToImage(output,lines,color);
    this.writeImage(output, name);
    output.release();
  }
 
  /**
   * draw the given lines to the given image with the given color
   * @param image - the image to draw to 
   * @param lines - the lines to draw
   * @param color - the color to use
   */
  public void drawLinesToImage(Mat image, Collection<Line> lines,
      Scalar color) {
    lines.stream().filter(Objects::nonNull).forEach(line -> Imgproc.line(image,
        toPoint(line.getPoint1()), toPoint(line.getPoint2()), color, 4));
  }

  /**
   * read an image from the given source
   * @param source
   * @return the image
   * @throws Exception 
   */
  public static Mat read(String source) throws Exception {
    File tmpFile=null;
    if (source.startsWith("http")) {
      String suffix = FilenameUtils.getExtension(source);
      tmpFile = File.createTempFile("image",suffix);
      URL url=new URL(source);
      FileUtils.copyURLToFile(url, tmpFile);
      source=tmpFile.getPath();
    }
    Mat frame=Imgcodecs.imread(source);
    if (tmpFile!=null)
      tmpFile.delete();
    return frame;
  }

}
