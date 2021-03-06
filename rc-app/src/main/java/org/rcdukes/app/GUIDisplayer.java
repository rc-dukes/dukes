package org.rcdukes.app;

import org.opencv.core.Mat;
import org.rcdukes.video.Image;
import org.rcdukes.video.ImageCollector;
import org.rcdukes.video.VideoRecorders;

import javafx.beans.property.Property;

/**
 * interface to make core gui display available for subGUIs
 *
 */
public interface GUIDisplayer {
  enum PictureStep{FIRST,PREV,NEXT,FORWARD};
  void displayOriginal(Image image);
  void displayOriginal( Mat openCvImage);
  void display1(Image image);
  void display2(Image image);
  void display3(Image image);
  void displayImageCollector(ImageCollector collector);
  void setImageCollector(ImageCollector imageCollector);
  void setMessageText(String text);
  void setCameraButtonText(String text);
  void setVideoRecorders(VideoRecorders videoRecorders);
  Property<String> getStartVideoProperty();
  void showFrameIndex(long frameIndex);
  /** 
   * handle the given error
   * @param th
   */
  void handle(Throwable th);

  
}
