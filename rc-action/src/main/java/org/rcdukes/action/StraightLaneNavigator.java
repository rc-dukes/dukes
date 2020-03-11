package org.rcdukes.action;

import java.util.Locale;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.rcdukes.common.Characters;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.geometry.LaneDetectionResult;
import org.rcdukes.geometry.Line;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.Message;
import stormbots.MiniPID;

/**
 * straight lane navigator
 *
 */
public class StraightLaneNavigator implements Navigator {

  boolean debug = false;
  protected static final Logger LOG = LoggerFactory
      .getLogger(StraightLaneNavigator.class);
  DukesVerticle sender;
  private TinkerGraph graph;

  public DukesVerticle getSender() {
    return sender;
  }

  public void setSender(DukesVerticle sender) {
    this.sender = sender;
  }

  /**
   * default constructor
   */
  public StraightLaneNavigator() {
    this.initDefaults();
    this.graph = TinkerGraph.open();
  }
  
  /**
   * access to graph 
   * @return the GraphTraversalSource
   */
  public GraphTraversalSource g() {
    return this.graph.traversal();
  }
  
  /**
   * set a property of the given vertex
   * @param v
   * @param name - name of the property
   * @param value - value of the property
   */
  public void setProp(Vertex v,String name,Object value) {
    if (value!=null) {
      v.property(name,value);
    }
  }

  /**
   * convert the given lane Detection Result to a vertex and add it to the graph
   * 
   * @param ldr
   * @return the new Vertex
   */
  public Vertex addToGraph(LaneDetectionResult ldr) {
    Vertex v = graph.addVertex(T.label,"pos", "frameIndex", ldr.frameIndex,
        "milliTimeStamp", ldr.milliTimeStamp);
    setProp(v,"left", ldr.left);
    setProp(v,"middle",ldr.middle);
    setProp(v,"right", ldr.right);
    setProp(v,"course", ldr.courseRelativeToHorizon);
    return v;
  }

  MiniPID pid;
  private Long tsLastLinesDetected;
  private Long tsLatestCommand;
  public static long COMMAND_LOOP_INTERVAL = 150L; // how often to send commands
                                                   // 3x per second
  private static final long MAX_DURATION_NO_LINES_DETECTED = 1200; // max two
                                                                   // seconds
                                                                   // @TODO
                                                                   // should be
                                                                   // speed
                                                                   // dependent
                                                                   // ...
  private static final long FALLBACK_TO_ANGLE_TIME = 450L;
  private boolean emergencyStopActivated = false;
  private long currentTime;

  /**
   * initialize my defaults
   */
  private void initDefaults() {
    pid = new MiniPID(1, 0, 0);
  }

  @Override
  public LaneDetectionResult fromJsonObject(JsonObject jo) {
    LaneDetectionResult ldr = jo.mapTo(LaneDetectionResult.class);
    return ldr;
  }

  enum AngleCheck {
    ok, empty, noLines
  }

  /**
   * check the angle
   * 
   * @param ldr
   * @param currentTime
   * @return the AngleCheck
   */
  private AngleCheck verifyAngleFound(LaneDetectionResult ldr,
      long currentTime) {
    AngleCheck result = AngleCheck.ok;
    if (ldr.middle == null && ldr.left == null && ldr.right == null) {
      // no angle detected
      if ((currentTime - tsLastLinesDetected > MAX_DURATION_NO_LINES_DETECTED)
          && !emergencyStopActivated) {
        String msg = String.format("no angle found for %d ms, emergency stop",
            MAX_DURATION_NO_LINES_DETECTED);
        LOG.warn(msg);
        result = AngleCheck.noLines;
      }
      result = AngleCheck.empty;
    } else {
      // all good
      tsLastLinesDetected = currentTime;
    }
    return result;
  }

  /**
   * is it o.k to send a command with the given angle?
   * 
   * @param angle
   * @return true if the angle is not null and enough time has passed since the
   *         latest command
   */
  public boolean cmdOk(Double angle) {
    return angle != null
        && currentTime - tsLatestCommand > COMMAND_LOOP_INTERVAL;
  }

  /**
   * shall we fallback to use an angle?
   * 
   * @return true if we haven't had a command recently and need one to avoid
   *         emergency stop
   */
  public boolean fallBackToAngle() {
    return currentTime - tsLatestCommand > FALLBACK_TO_ANGLE_TIME;
  }

  /**
   * process the laneDetectResult
   * 
   * @param ldr
   *          - the lane detection result
   * @return - a message to be sent to the vehicle or null on error
   */
  @Override
  public JsonObject getNavigationInstruction(LaneDetectionResult ldr) {
    Vertex posV = addToGraph(ldr);
    currentTime = ldr.milliTimeStamp;
    if (tsLastLinesDetected == null)
      tsLastLinesDetected = currentTime;
    if (tsLatestCommand == null)
      tsLatestCommand = currentTime;

    AngleCheck angleCheck = verifyAngleFound(ldr, currentTime);
    switch (angleCheck) {
    case empty:
      return null;
    case noLines:
      return ActionVerticle.emergencyStopCommand();
    default:
      // ok - do nothing
    }

    Double angle = null;
    Double angleFactor = 0.5;
    if (ldr.courseRelativeToHorizon != null) {
      // pass 1: steer on courseRelativeToHorizon
      angle = Math.toDegrees(ldr.courseRelativeToHorizon);
    } else if (this.fallBackToAngle()) {
      if (ldr.left != null && ldr.right == null) {
        angle = ldr.left * angleFactor;
      }
      if (ldr.right != null && ldr.left == null) {
        angle = ldr.right * angleFactor;
      }
    }
    if (this.cmdOk(angle)) {
      String msg = ldr.debugInfo()
          + String.format("\nsteer: %s", Line.angleString(angle));
      LOG.debug(msg);
      tsLatestCommand = currentTime;
      JsonObject message = steerCommand(-(angle));
      return message;
    }
    return null;
  }

  /**
   * get the command to steer the vehicle
   * 
   * @param angle
   * @return - the command message
   */
  public JsonObject steerCommand(Double angle) {
    String angleStr = String.format(Locale.ENGLISH, "%5.1f°", angle);
    JsonObject message = new JsonObject().put("type", "servoAngle").put("angle",
        angle);
    String debugMsg = String.format("sending servoAngle %s", angleStr);
    if (debug)
      LOG.debug(debugMsg);
    return message;
  }

  @Override
  public void navigateWithInstruction(JsonObject navigationInstruction) {
    if (navigationInstruction != null)
      sender.send(Characters.BO, navigationInstruction);
  }

  @Override
  public void navigateWithMessage(Message<JsonObject> ldrMessage) {
    LaneDetectionResult ldr = fromJsonObject(ldrMessage.body());
    this.navigateWithLaneDetectionResult(ldr);
  }

  @Override
  public void navigateWithLaneDetectionResult(LaneDetectionResult ldr) {
    JsonObject navigationJo = getNavigationInstruction(ldr);
    navigateWithInstruction(navigationJo);
  }

}
