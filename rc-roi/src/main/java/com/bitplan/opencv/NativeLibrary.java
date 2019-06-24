package com.bitplan.opencv;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.opencv.core.Core;

/**
 * load OpenCV NativeLibrary properly
 */
public class NativeLibrary {
  protected static File nativeLibPath = new File("../lib");

  /**
   * get the native library path
   * 
   * @return the file for the native library
   */
  public static File getNativeLibPath() {
    return nativeLibPath;
  }

  /**
   * set the native library path
   * 
   * @param pNativeLibPath
   *          - the library path to use
   */
  public static void setNativeLibPath(File pNativeLibPath) {
    nativeLibPath = pNativeLibPath;
  }

  /**
   * get the current library path
   * 
   * @return the current library path
   */
  public static String getCurrentLibraryPath() {
    return System.getProperty("java.library.path");
  }

  /**
   * Adds the specified path to the java library path
   *
   * @param pathToAdd
   *          the path to add
   * @throws Exception
   * @see <a href=
   *      'https://stackoverflow.com/questions/15409223/adding-new-paths-for-native-libraries-at-runtime-in-java'>Stackoverflow
   *      question how to add path entry to native library search path at
   *      runtime</a>
   */
  public static void addLibraryPath(String pathToAdd) throws Exception {
    final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
    usrPathsField.setAccessible(true);

    // get array of paths
    final String[] paths = (String[]) usrPathsField.get(null);

    // check if the path to add is already present
    for (String path : paths) {
      if (path.equals(pathToAdd)) {
        return;
      }
    }

    // add the new path
    final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
    newPaths[newPaths.length - 1] = pathToAdd;
    usrPathsField.set(null, newPaths);
  }

  public static File getNativeLib() {
    File nativeLib = new File(getNativeLibPath(),
        "lib" + Core.NATIVE_LIBRARY_NAME + ".dylib");
    return nativeLib;
  }

  /**
   * load the native library by adding the proper library path
   * 
   * @throws Exception
   *           - if reflection access fails (e.g. in Java9/10)
   */
  public static void load() throws Exception {
    addLibraryPath(getNativeLibPath().getAbsolutePath());
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
  }

}
