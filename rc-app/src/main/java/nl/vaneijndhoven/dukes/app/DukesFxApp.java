package nl.vaneijndhoven.dukes.app;

import com.bitplan.opencv.NativeLibrary;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
/**
 * Java FX based GUI 
 * @author wf
 *
 */
public class DukesFxApp extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    VBox root = FXMLLoader
        .load(getClass().getClassLoader().getResource("fx/dukes.fxml"));
    Scene scene = new Scene(root, 1920*2/3, 1080*2/3);
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
