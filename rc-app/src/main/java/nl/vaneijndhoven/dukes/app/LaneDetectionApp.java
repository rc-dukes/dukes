package nl.vaneijndhoven.dukes.app;

import com.bitplan.opencv.NativeLibrary;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import nl.vaneijndhoven.dukes.common.Config;
import nl.vaneijndhoven.dukes.common.Environment;

/**
 * JavaFx Lane Detection App
 *
 */
@Deprecated
public class LaneDetectionApp extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    // fxml files are in rc-detect
    BorderPane root = FXMLLoader
        .load(getClass().getClassLoader().getResource("fx/lanedetection.fxml"));
    Scene scene = new Scene(root, 1000, 800);
    primaryStage.setTitle("Dukes lane detection");
    primaryStage.setScene(scene);
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
