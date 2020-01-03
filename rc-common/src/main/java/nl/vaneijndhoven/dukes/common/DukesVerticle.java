package nl.vaneijndhoven.dukes.common;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
/**
 * 
 * @author wf
 * unifies handling of AbstracVerticles in rc-dukes project
 */
public class DukesVerticle extends AbstractVerticle {
  protected boolean started=false;

  /**
   * is this AbstractVerticle already started?
   * @return true if started
   */
  public boolean isStarted() {
    return started;
  }
  
  /**
   * wait for me to be started
   * @param timeOut - in msecs
   * @param pollTime - in msec
   * @throws Exception 
   */
  public void waitStarted(int timeOut, int pollTime) throws Exception {
    int leftTime=timeOut;
    while (!this.isStarted()) {
      Thread.sleep(pollTime);
      leftTime-=pollTime;
      if (leftTime<0) {
        String msg=String.format("waitStarted timed out after %d msecs",timeOut);
        throw new Exception(msg);
      }
    }
  }
  
  /**
   * send a message to the given receive via the event bus
   * @param receiver
   * @param namevalues
   */
  public void send(Characters receiver, String ...nameValues) {
    JsonObject jo = new JsonObject();
    if (nameValues.length%2 !=0) {
      String msg=String.format("got %d namevalue but expected even number",nameValues.length);
      throw new IllegalArgumentException(msg);
    }
    for (int i=0;i<nameValues.length;i+=2) {
      String name=nameValues[i];
      String value=nameValues[i+1];
      jo.put(name,value);
    }
    getVertx().eventBus().send(receiver.getCallsign(),jo);
  }
}
