package nl.vaneijndhoven.opencv.video;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.opencv.core.Core;

import com.bitplan.opencv.NativeLibrary;

public class LaneDetectionApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = FXMLLoader.load(getClass().getClassLoader().getResource("fx/lanedetection.fxml"));
        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setTitle("Dukes lane detection");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String... args) throws Exception {
        NativeLibrary.load();
        launch(args);
    }

}
