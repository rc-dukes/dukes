package nl.vaneijndhoven.dukes.common;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorHandler {
  /**
   * get the stack trace for the given exception
   * 
   * @param th
   * @return - the stack trace
   */
  public static String getStackTraceText(Throwable th) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    th.printStackTrace(pw);
    String exceptionText = sw.toString();
    return exceptionText;
  }
}
