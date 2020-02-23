package org.rcdukes.app;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * a Slider with a Label and a value
 * 
 * @author wf
 *
 */
public class LabeledValueSlider extends HBox {
  public static boolean debug=true;
  protected static final Logger LOG = LoggerFactory
      .getLogger(LabeledValueSlider.class);
  @FXML
  private Label label;
  @FXML
  private Slider slider;
  @FXML
  private TextField textField;
  
  String format="%.2f";
  
  
  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public double getBlockIncrement() {
    return slider.getBlockIncrement();
  }
  
  public void setBlockIncrement(double value) {
    slider.setBlockIncrement(value);
  }
  
  public double getMax() {
    return slider.getMax();
  }
  
  public void setMax(double value) {
    slider.setMax(value);
  }
  
  public double getMin() {
    return slider.getMin();
  }
  
  public void setMin(double value) {
    slider.setMin(value);
  }
  
  public double getValue() {
    return slider.getValue();
  }
  
  public void setValue(double value) {
    slider.setValue(value);
  }
  
  public String getText() {
    return label.getText();
  }

  public void setText(String pLabelText) {
    label.setText(pLabelText);
  }
  
  public URL  getResource(String path) {
    return getClass().getClassLoader().getResource(path);
  }

  /**
   * construct me
   * see https://docs.oracle.com/javase/9/docs/api/javafx/fxml/doc-files/introduction_to_fxml.html#custom_components
   */
  public LabeledValueSlider() {
    FXMLLoader fxmlLoader = new FXMLLoader(
        getResource("fx/labeledvalueslider.fxml"));
    try {
      // let's load the HBox - fxmlLoader doesn't know anything about us yet
      fxmlLoader.setController(this); 
      fxmlLoader.setRoot(this);
      Object loaded = fxmlLoader.load();
      Object root=fxmlLoader.getRoot();
     
      if (debug) {
        String msg=String.format("%s loaded for root %s", loaded.getClass().getName(),root.getClass().getName());
        LOG.info(msg);
      }
      textField.textProperty().bind(slider.valueProperty().asString(format));
      textField.setAlignment(Pos.CENTER_RIGHT);
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }
}
