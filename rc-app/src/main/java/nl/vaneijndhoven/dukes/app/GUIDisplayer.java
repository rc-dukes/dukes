package nl.vaneijndhoven.dukes.app;

import org.opencv.core.Mat;

import javafx.beans.property.StringProperty;

/**
 * interface to make core gui display available for subGUIs
 *
 */
public interface GUIDisplayer {
  void displayOriginal(byte[] imageFrame);
  void displayOriginal( Mat openCvImage);
  void display1(byte[] imageFrame);
  void display2(byte[] imageFrame);
  void setCameraButtonText(String text);
  void showCurrentValues(String text);
  StringProperty getLaneVideoProperty();
  StringProperty getStartVideoProperty();
}
