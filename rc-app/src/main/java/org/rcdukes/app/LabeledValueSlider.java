package org.rcdukes.app;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 * custom control see
 * https://noblecodemonkeys.com/javafx-custom-controls-and-scenebuilder/
 * 
 * @author wf
 *
 */
public class LabeledValueSlider extends HBox {

  @FXML
  private Label label;
  @FXML
  private Slider slider;
  @FXML
  private TextField textField;

  private Node view;
  private LabeledValueSlider controller;


  public String getLabelText() {
    return label.getText();
  }

  public void setLabelText(String labelText) {
    this.label.setText(labelText);
  }

  public LabeledValueSlider() {
    FXMLLoader fxmlLoader = new FXMLLoader(
        getClass().getResource("labeledvalueslider.fxml"));
    fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
     
      @Override
      public Object call(Class<?> param) {
        controller = new LabeledValueSlider();
        return controller;
      }
    });
    try {
      view = (Node) fxmlLoader.load();

    } catch (IOException ex) {
    }
    getChildren().add(view);
  }

}
