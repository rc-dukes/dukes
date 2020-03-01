package org.rcdukes.app;

import io.vertx.core.json.JsonObject;

/**
 * Eventbus loggging functions
 * @author wf
 *
 */
public interface EventbusLogger {
  /**
   * other events
   * @param jo
   */
  public void logEvent(JsonObject jo);
}
