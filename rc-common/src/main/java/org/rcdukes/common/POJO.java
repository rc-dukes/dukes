package org.rcdukes.common;
import io.vertx.core.json.JsonObject;

/**
 * Plain old Java Object handling interface to be used with vert.x
 * @author wf
 *
 */
public interface POJO {
  
  public default String asJson() {
    return this.asJsonObject().toString();
  }
  
  public default JsonObject asJsonObject() {
    return JsonObject.mapFrom(this);
  }
}
