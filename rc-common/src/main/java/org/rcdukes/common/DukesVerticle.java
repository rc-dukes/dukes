package org.rcdukes.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;

/**
 * 
 * @author wf unifies handling of AbstracVerticles in rc-dukes project uses the
 *         rxjava version of AbstractVerticles
 */
public abstract class DukesVerticle extends AbstractVerticle {
  protected static final Logger LOG = LoggerFactory
      .getLogger(DukesVerticle.class);

  public enum Status {
    created, started, stopped
  }

  private Status status = Status.created;

  public Status getStatus() {
    return status;
  }

  protected Characters character;
  protected String deploymentID = null;

  /**
   * construct me
   * 
   * @param character
   */
  public DukesVerticle(Characters character) {
    this.character = character;
  }

  public void logStatus(String op, String status) {
    String msg = String.format("%s %s: %s", op, status,
        character.description());
    LOG.info(msg);
  }

  /**
   * actions to be done before starting the DukesVerticle e.g. logging a message
   * with the intention to start
   */
  public void preStart() {
    logStatus("Starting", "");
  }

  public void preStop() {
    logStatus("Stopping", "");
  }

  /**
   * actions to be done after starting the DukesVerticle e.g. logging a message
   * with the info that the start was successful
   */
  public void postStart() {
    this.status = Status.started;
    logStatus("Start", "successful");
  }

  public void postStop() {
    this.status = Status.stopped;
    logStatus("Stop", "successful");
  }

  /**
   * wait for me to be started
   * 
   * @param timeOut
   *          - in msecs
   * @param pollTime
   *          - in msec
   * @throws Exception
   */
  public void waitStatus(Status status, int timeOut, int pollTime)
      throws Exception {
    int leftTime = timeOut;
    while (!this.status.equals(status)) {
      Thread.sleep(pollTime);
      leftTime -= pollTime;
      if (leftTime < 0) {
        String msg = String.format("wait %s timed out after %d msecs",
            status.name(), timeOut);
        throw new Exception(msg);
      }
    }
  }

  /**
   * convert the given list of name Value to a JsonObject
   * 
   * @param nameValues
   *          - needs to be an even number of arguments otherwise an
   *          IllegalArgumentException runtime exception is thrown
   * @return - the JsonObject create from the nameValues by putting name,value
   *         into the object in a pairwise fassion
   */
  public JsonObject toJsonObject(String... nameValues) {
    JsonObject jo = new JsonObject();
    if (nameValues.length % 2 != 0) {
      String msg = String.format("got %d namevalue but expected even number",
          nameValues.length);
      throw new IllegalArgumentException(msg);
    }
    for (int i = 0; i < nameValues.length; i += 2) {
      String name = nameValues[i];
      String value = nameValues[i + 1];
      jo.put(name, value);
    }
    return jo;
  }

  /**
   * send a message to the given receiver via the event bus
   * 
   * @param receiver
   *          the character to send a message to
   * @param nameValues
   *          - the content of the message as name-value pairs
   */
  public void send(Characters receiver, String... nameValues) {
    JsonObject jo = this.toJsonObject(nameValues);
    String address = receiver.getCallsign();
    send(address,jo);
  }
    
  /**
   * send the given json object to the given address over the vertx event bus
   * @param address - the address to send to
   * @param jo - the JsonObject to send
   */
  public void send(String address,JsonObject jo)  {
    getVertx().eventBus().publish(address, jo);
  }
  
  /**
   * get the address for the given event and my Callsigng
   * @param event - the event
   * @return - the address to be used
   */
  public String getEventAddress(Events event) {
    // rc-dukes convention - watch webcontrol javascript ...
    String address = character.getCallsign() + ":" + event.name();
    return address;
  }
  
  /**
   * send the given even with the given Json Object
   * @param event
   * @param jo
   */
  public void sendEvent(Events event,JsonObject jo) {
    String address = this.getEventAddress(event);
    send(address,jo);
  }

  /**
   * enable a consumer to handle the given event
   * 
   * @param event
   * @param handler
   */
  public <T> void consumer(Events event, Handler<Message<T>> handler) {
    String address = this.getEventAddress(event);
    vertx.eventBus().consumer(address, handler);
  }

  /**
   * share the data for the given jsonobject under the given key
   * - if the json 
   * @param key - the key for the JsonObject
   * @param jo   the json object
   */
  public void shareData(String key, JsonObject jo) {
    if (jo==null) return;
    String msg = String.format("sharing %s: %s", key, jo.encodePrettily());
    LOG.info(msg);
    vertx.sharedData().getLocalMap(character.getCallsign()).put(key, jo);
  }

  /**
   * get the shared data for the given key
   * 
   * @param key
   * @return the corresponding json object
   */
  public JsonObject getSharedData(String key) {
    JsonObject jo = (JsonObject) vertx.sharedData()
        .getLocalMap(character.getCallsign()).get(key);
    return jo;
  }
  
  /**
   * get a pojo from the shared data
   * @param key
   * @param clazz
   * @return - the pojo
   */
  public <T> T getSharedPojo(String key, Class<T> clazz) {
    JsonObject jo=getSharedData(key);
    T result=fromJsonObject(jo,clazz);
    return result;
  }
  
  
  /**
   * construct a Pojo from the given JsonObject
   * @param jo - the JsonObject
   * @param clazz - the type to use
   * @return - the mapped instance or a default instance from a no-args constructor if jo is null
   */
  public <T> T fromJsonObject(JsonObject jo,
      Class<T> clazz)  {
    T result;
    if (jo==null) {
      try {
        result=clazz.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        LOG.trace(e.getMessage());
        result=null;
      }
    } else {
      result=jo.mapTo(clazz);
    }
    return result;
  }

}
