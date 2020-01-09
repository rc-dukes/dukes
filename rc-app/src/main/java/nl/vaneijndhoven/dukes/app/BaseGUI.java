package nl.vaneijndhoven.dukes.app;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.opencv.core.Mat;

import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.GlyphsDude;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import nl.vaneijndhoven.dukes.common.ErrorHandler;
import nl.vaneijndhoven.opencv.video.ImageUtils;

/**
 * base java fx GUI functionality
 * @author wf
 *
 */
public class BaseGUI {
  GUIDisplayer displayer;

  public GUIDisplayer getDisplayer() {
    return displayer;
  }

  public void setDisplayer(GUIDisplayer displayer) {
    this.displayer = displayer;
  }
  
  public static double getScreenWidth() {
    return Screen.getPrimary().getVisualBounds().getWidth();
  }

  public static double getScreenHeight() {
    return Screen.getPrimary().getVisualBounds().getHeight();
  }

  /**
   * show the given imageFrame in the given JavaFX imageView Control
   * 
   * @param imageView
   * @param imageFrame
   */
  protected void displayImage(ImageView imageView, byte[] imageFrame) {
    if (imageFrame == null)
      return;
    Image image = new Image(new ByteArrayInputStream(imageFrame));
    this.onFXThread(imageView.imageProperty(), image);
  }

  protected void displayImage(ImageView fxImage, Mat openCvImage) {
    if (openCvImage.rows() > 0) {
      Image image = ImageUtils.mat2Image(openCvImage,".png");
      if (image!=null)
        this.onFXThread(fxImage.imageProperty(), image);
    }
  }

  /**
   * Set typical {@link ImageView} properties: a fixed width and the information
   * to preserve the original image ration
   *
   * @param imageView
   *          the {@link ImageView} to use
   * @param width
   *          the width of the image to set
   */
  protected void imageViewProperties(ImageView imageView, int width) {
    // preserve the image ratio
    imageView.setPreserveRatio(true);
    // set a fixed width for the given ImageView
    imageView.setFitWidth(width);
  }

  /**
   * Generic method for putting element running on a non-JavaFX thread on the
   * JavaFX thread, to properly update the UI
   *
   * @param property
   *          a {@link ObjectProperty}
   * @param value
   *          the value to set for the given {@link ObjectProperty}
   */
  protected <T> void onFXThread(final ObjectProperty<T> property,
      final T value) {
    if (Platform.isFxApplicationThread()) {
      property.set(value);
    } else {
      Platform.runLater(() -> property.set(value));
    }
  }
  
  /**
   * browse the given url
   * @param url
   */
  protected void browse(String url) {
    try {
      Desktop.getDesktop().browse(new URI(url));
    } catch (IOException | URISyntaxException e) {
      handle(e);
    }
  }
  
  public void handle(Throwable th) {
    String text = ErrorHandler.getStackTraceText(th);
    displayer.setMessageText(text);
  }
  
  /**
   * set the icon for the given button
   * @param button  the button to modify
   * @param icon - the icon to use
   * @param color - the color of the icon
   * @param bgColor - the background color of the icon
   */
  public void setButtonIcon(Button button,GlyphIcons icon,String color,String bgColor) {
    GlyphsDude.setIcon(button, icon,"2.5em");
    String text=button.getText();
    button.setText("");
    button.setTooltip(new Tooltip(text));
    button.setStyle(String.format("-fx-fill: %s;-fx-background-color: %s;",color,bgColor));
    String style=button.getStyle();
    System.out.println(String.format("button %s has style %s", text,style));
  }
}
