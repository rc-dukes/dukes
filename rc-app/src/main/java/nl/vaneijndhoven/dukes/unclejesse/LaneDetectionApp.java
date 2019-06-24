package nl.vaneijndhoven.dukes.unclejesse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * JavaFx Lane Detection App 
 *
 */
@Deprecated 
public class LaneDetectionApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = FXMLLoader.load(getClass().getClassLoader().getResource("fx/lanedetection.fxml"));
        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setTitle("Dukes lane detection");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String... args) {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }

}
