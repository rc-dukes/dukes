package nl.vaneijndhoven.dukes.common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;

/**
 * 
 * @author wf
 * unifies handling of AbstracVerticles in rc-dukes project
 * uses the rxjava version of AbstractVerticles
 */
public abstract class DukesVerticle extends AbstractVerticle {
  protected static final Logger LOG = LoggerFactory.getLogger(DukesVerticle.class);
  
  public enum Status{created,started,stopped}
  private Status status=Status.created;
  
  public Status getStatus() {
    return status;
  }

  protected Characters  character;
  protected String deploymentID=null;
  
  /**
   * construct me
   * @param character
   */
  public DukesVerticle(Characters character) {
    this.character=character;
  }
  
  public void logStatus(String op, String status) {
    String msg=String.format("%s %s: %s",op,status,character.description());
    LOG.info(msg);
  }
  /**
   * actions to be done before starting the DukesVerticle
   * e.g. logging a message with the intention to start
   */
  public void preStart() {
    logStatus("Starting","");
  }
  
  public void preStop() {
    logStatus("Stopping","");
  }
  
  /**
   * actions to be done after starting the DukesVerticle
   * e.g. logging a message with the info that the start was successful
   */
  public void postStart() {
    this.status=Status.started;
    logStatus("Start","successful");
  }
  
  public void postStop() {
    this.status=Status.stopped;
    logStatus("Stop","successful");
  }
  
  /**
   * wait for me to be started
   * @param timeOut - in msecs
   * @param pollTime - in msec
   * @throws Exception 
   */
  public void waitStatus(Status status,int timeOut, int pollTime) throws Exception {
    int leftTime=timeOut;
    while (!this.status.equals(status)) {
      Thread.sleep(pollTime);
      leftTime-=pollTime;
      if (leftTime<0) {
        String msg=String.format("wait %s timed out after %d msecs",status.name(),timeOut);
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
   * @param receiver the character to send a message to
   * @param nameValues - the content of the message as name-value pairs
   */
  public void send(Characters receiver, String ...nameValues) {
    JsonObject jo = this.toJsonObject(nameValues);
    String address=receiver.getCallsign();
    getVertx().eventBus().send(address,jo);
  }
  
  /**
   * enable a consumer to handle the given event
   * @param event
   * @param handler
   */
  public <T> void consumer(Events event, Handler<Message<T>> handler) {
    // rc-dukes convention - watch webcontrol javascript ...
    String address=character.getCallsign() + ":"+event.name();
    vertx.eventBus().consumer(address,handler);
  }
  
}
