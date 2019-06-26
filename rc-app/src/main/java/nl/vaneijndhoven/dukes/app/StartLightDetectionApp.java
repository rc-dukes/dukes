package nl.vaneijndhoven.dukes.app;

import com.bitplan.opencv.NativeLibrary;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/***
 * JavaFx based start light detection app
 *
 */
@Deprecated
public class StartLightDetectionApp extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    BorderPane root = FXMLLoader.load(
        getClass().getClassLoader().getResource("fx/startlightdetection.fxml"));
    Scene scene = new Scene(root, 850, 600);
    primaryStage.setTitle("Dukes start light detection");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * App for start light dectection
   * @param args
   * @throws Exception
   */
  public static void main(String... args) throws Exception {
    NativeLibrary.load();
    launch(args);
  }

}
