package org.rcdukes.app;

import org.opencv.core.Mat;
import org.rcdukes.video.Image;

import javafx.beans.property.StringProperty;

/**
 * interface to make core gui display available for subGUIs
 *
 */
public interface GUIDisplayer {
  void displayOriginal(Image image);
  void displayOriginal( Mat openCvImage);
  void display1(Image image);
  void display2(Image image);
  void setMessageText(String text);
  void setCameraButtonText(String text);
  void showCurrentValues(String text);
  StringProperty getLaneVideoProperty();
  StringProperty getStartVideoProperty();
  /** 
   * handle the given error
   * @param th
   */
  void handle(Throwable th);
}
