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
    // see https://stackoverflow.com/questions/39164050/javafx-8-tabpanes-and-tabs-with-separate-fxml-and-controllers-for-each-tab
    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fx/dukes.fxml"));
    VBox root = loader.load();
    Scene scene = new Scene(root, 1920*2/3, 1080*2/3);
    DukesFxGUI gui = loader.getController();
    gui.init(primaryStage);
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
