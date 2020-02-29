package org.rcdukes.app;

import java.net.URL;

import org.rcdukes.opencv.NativeLibrary;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
/**
 * Java FX based GUI 
 * @author wf
 *
 */
public class DukesFxApp extends Application {
  
  public URL  getResource(String path) {
    return getClass().getClassLoader().getResource(path);
  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    // see https://stackoverflow.com/questions/39164050/javafx-8-tabpanes-and-tabs-with-separate-fxml-and-controllers-for-each-tab
    FXMLLoader loader = new FXMLLoader(getResource("fx/dukes.fxml"));   
    GridPane root = loader.load();
    Scene scene = new Scene(root, BaseGUI.getScreenWidth()*3/4, BaseGUI.getScreenHeight()*3/4);
    String css = getResource("fx/dukes.css").toExternalForm(); 
    scene.getStylesheets().add(css);
    DukesFxGUI gui = loader.getController();
    gui.init(primaryStage);
    primaryStage.setTitle("RC-Dukes Self driving Car");
    primaryStage.setScene(scene);
    primaryStage.setMaximized(true);
    primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
      gui.addJustFullScreenButton();
    });
    primaryStage.show();
  }
  
  /**
   * run the app
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String... args) throws Exception {
    NativeLibrary.load();
    launch(args);
  }
}
