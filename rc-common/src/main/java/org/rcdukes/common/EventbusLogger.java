package org.rcdukes.common;

import io.vertx.core.json.JsonObject;

/**
 * Eventbus logging functions
 * @author wf
 *
 */
public interface EventbusLogger {
  /**
   * log the given Event
   * @param address
   * @param jo
   */
  public void logEvent(String address,JsonObject jo);

  /**
   * log the given message
   * @param msg
   */
  public void log(String msg);
}
