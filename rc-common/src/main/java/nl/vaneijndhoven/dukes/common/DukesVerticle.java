package nl.vaneijndhoven.dukes.common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;

/**
 * 
 * @author wf
 * unifies handling of AbstracVerticles in rc-dukes project
 * uses the rxjava version of AbstractVerticles
 */
public class DukesVerticle extends AbstractVerticle {
  protected static final Logger LOG = LoggerFactory.getLogger(DukesVerticle.class);
  
  private boolean started=false;
  protected Characters  character;
  
  public DukesVerticle(Characters character) {
    this.character=character;
  }

  /**
   * is this AbstractVerticle already started?
   * @return true if started
   */
  public boolean isStarted() {
    return started;
  }
  
  public void preStart() {
    String msg=String.format("Starting %s",character.description());
    LOG.info(msg);
  }
  
  public void postStart() {
    this.started=true;
    String msg=String.format("%s started",character.description());
    LOG.info(msg);
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
   * convert the given list of name Value to a JsonObject
   * @param nameValues - needs to be an even number of arguments otherwise
   * an IllegalArgumentException runtime exception is thrown
   * @return - the JsonObject create from the nameValues by putting
   * name,value into the object in a pairwise fassion 
   */
  public JsonObject toJsonObject(String ...nameValues) {
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
    return jo;
  }
  
  /**
   * send a message to the given receiver via the event bus
   * @param receiver the character to sendd a message to
   * @param nameValues - the content of the message as name-value pairs
   */
  public void send(Characters receiver, String ...nameValues) {
    JsonObject jo = this.toJsonObject(nameValues);
    getVertx().eventBus().send(receiver.getCallsign(),jo);
  }
}
