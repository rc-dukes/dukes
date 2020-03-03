package org.rcdukes.app;

import java.net.URL;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.rcdukes.opencv.NativeLibrary;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Java FX based GUI
 * 
 * @author wf
 *
 */
public class DukesFxApp extends Application {

  private static String[] cmdLineArgs;
  private CmdLineParser parser;
  @Option(name = "-d", aliases = {
      "--debug" }, usage = "debug\ncreate additional debug output if this switch is used")
  protected boolean debug = false;

  @Option(name = "-a", aliases = {
      "--autostart" }, usage = "autostart using the configuration")
  protected boolean autostart=false;

  /**
   * get the resource from the given path
   * 
   * @param path
   *          - relative to src/main/resource in the target classpath
   * @return - the URL for the resource
   */
  public URL getResource(String path) {
    return getClass().getClassLoader().getResource(path);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    this.parseArguments();
    // see
    // https://stackoverflow.com/questions/39164050/javafx-8-tabpanes-and-tabs-with-separate-fxml-and-controllers-for-each-tab
    FXMLLoader loader = new FXMLLoader(getResource("fx/dukes.fxml"));
    VBox root = loader.load();
    Scene scene = new Scene(root, BaseGUI.getScreenWidth() * 3 / 4,
        BaseGUI.getScreenHeight() * 3 / 4);
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
    if (autostart) {
      gui.autoStart();
    }
  }

  /**
   * parse the command line arguments
   * 
   * @param args
   * @throws CmdLineException
   */
  public void parseArguments() throws CmdLineException {
    parser = new CmdLineParser(this);
    parser.parseArgument(cmdLineArgs);
  }

  /**
   * run the app
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String... args) throws Exception {
    cmdLineArgs = args;
    NativeLibrary.load();
    launch(args);
  }
}
