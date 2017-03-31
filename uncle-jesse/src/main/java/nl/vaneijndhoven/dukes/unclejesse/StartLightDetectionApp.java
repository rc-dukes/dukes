//package nl.vaneijndhoven.dukes.unclejesse;
//
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.scene.layout.BorderPane;
//import javafx.stage.Stage;
//import org.opencv.core.Core;
//
//public class StartLightDetectionApp extends Application {
//
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        BorderPane root = FXMLLoader.load(getClass().getClassLoader().getResource("fx/startlightdetection.fxml"));
//        Scene scene = new Scene(root, 850, 600);
//        primaryStage.setTitle("Dukes start light detection");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    public static void main(String... args) {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        launch(args);
//    }
//
//}
