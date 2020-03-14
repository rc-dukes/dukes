package org.rcdukes.common;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * test the ErrorHandler
 * 
 * @author wf
 */
public class TestErrorHandler {

  @Test
  public void testErrorHandler() {
    String trace=null;
    try {
      throw new Exception("oops - a problem");
    } catch (Throwable th) {
      trace=ErrorHandler.getStackTraceText(th);
    }
    assertNotNull(trace);
    assertTrue(trace.contains("oops - a problem"));
    assertTrue(trace.contains("TestErrorHandler.java:18"));
  }

}
