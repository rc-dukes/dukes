package org.rcdukes.action;

import java.util.List;
import java.util.Locale;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
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
    this.graph.createIndex("frameIndex", Vertex.class);
    this.graph.createIndex("milliTimeStamp", Vertex.class);
  }

  /**
   * access to graph
   * 
   * @return the GraphTraversalSource
   */
  public GraphTraversalSource g() {
    return this.graph.traversal();
  }

  /**
   * set a property of the given vertex
   * 
   * @param v
   * @param name
   *          - name of the property
   * @param value
   *          - value of the property
   */
  public void setProp(Vertex v, String name, Object value) {
    if (value != null) {
      v.property(name, value);
    }
  }

  /**
   * convert the given lane Detection Result to a vertex and add it to the graph
   * 
   * @param ldr
   * @return the new Vertex
   */
  public Vertex addToGraph(LaneDetectionResult ldr) {
    Vertex v = graph.addVertex(T.label, "pos", "frameIndex", ldr.frameIndex,
        "milliTimeStamp", ldr.milliTimeStamp);
    setProp(v, "left", ldr.left);
    setProp(v, "middle", ldr.middle);
    setProp(v, "right", ldr.right);
    setProp(v, "course", ldr.courseRelativeToHorizon);
    return v;
  }

  MiniPID pid;
  private Long tsLatestCommand;
  public static long COMMAND_LOOP_INTERVAL = 150L; // how often to send commands
                                                   // 3x per second
  private static final int MAX_DURATION_NO_LINES_DETECTED = 1500; // max 1.5
                                                                  // secs
                                                                  // @TODO
                                                                  // should be
                                                                  // speed
                                                                  // dependent
                                                                  // ...
  private boolean emergencyStopActivated = false;
  private long currentTime;
  private Long startTime;

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

  class AngleRange {
    Double min = Double.MAX_VALUE;
    Double max = -Double.MAX_VALUE;
    double sum = 0;
    Double avg = null;
    int count;
    int timeWindow;
    private String name;
    private Double stdDev;

    /**
     * create an angle Range for the given vertices
     * 
     * @param name
     * @param currentTime
     * @param timeWindow
     */
    public AngleRange(String name, long currentTime, int timeWindow) {
      this.name = name;
      List<Vertex> angleNodes = g().V().has(name)
          .has("milliTimeStamp", P.gt(currentTime - timeWindow)).toList();
      count = angleNodes.size();
      this.timeWindow = timeWindow;
      if (count==0) {
        min=null;
        max=null;
        stdDev=null;
      }

      for (Vertex angleNode : angleNodes) {
        double angle = (Double) angleNode.property(name).value();
        sum += angle;
        max = Math.max(max, angle);
        min = Math.min(min, angle);
      }
      avg = sum / count;
      double sum2 = 0;
      for (Vertex angleNode : angleNodes) {
        Double angle = (Double) angleNode.property(name).value();
        Double avgDiff = angle - avg;
        sum2 += avgDiff * avgDiff;
      }
      stdDev = Math.sqrt(sum2 / count);
    }

    /**
     * get the debug info
     * 
     * @return
     */
    public String debugInfo() {
      String info = String.format("%6s: %s <- %s ± %s -> %s / %3d in %3d msecs",
          name, Line.angleString(min), Line.angleString(avg),
          Line.angleString(stdDev), Line.angleString(max), count, timeWindow);
      return info;
    }
  }

  /**
   * set the time stamps
   * 
   * @param ldr
   */
  public void setTime(LaneDetectionResult ldr) {
    currentTime = ldr.milliTimeStamp;
    if (startTime == null)
      startTime = currentTime;
    if (tsLatestCommand == null)
      tsLatestCommand = currentTime;
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
    if (this.emergencyStopActivated) return null;
    // set the current time and start time
    setTime(ldr);
    // add the lane detection result to the tinker graph
    addToGraph(ldr);
    // analyze the LaneDetectionResults of the relevant time Windows
    int timeWindow = 1000;
    AngleRange middleRange = new AngleRange("middle", currentTime, timeWindow);
    AngleRange stopRange = new AngleRange("middle", currentTime,
        MAX_DURATION_NO_LINES_DETECTED);
    AngleRange leftRange = new AngleRange("left", currentTime, timeWindow);
    AngleRange rightRange = new AngleRange("right", currentTime, timeWindow);
    AngleRange courseRange = new AngleRange("course", currentTime, timeWindow);
    LOG.info(stopRange.debugInfo());
    LOG.info(middleRange.debugInfo());
    LOG.info(leftRange.debugInfo());
    LOG.info(rightRange.debugInfo());
    LOG.info(courseRange.debugInfo());
    if (currentTime - startTime > MAX_DURATION_NO_LINES_DETECTED
        && stopRange.count == 0 || emergencyStopActivated) {
      this.emergencyStopActivated = true;
      return ActionVerticle.emergencyStopCommand();
    }
    int MIN_FOUND_PER_TIMEWINDOW = 5;
    JsonObject message=null;
    if (middleRange.count > MIN_FOUND_PER_TIMEWINDOW) {
      double angle = courseRange.avg;
      if (this.cmdOk(angle)) {
        String msg = ldr.debugInfo()
            + String.format("\nsteer: %s", Line.angleString(angle));
        LOG.debug(msg);
        tsLatestCommand = currentTime;
        // @TODO check polarity
        message = steerCommand(-(angle));
      }
    }
    return message;
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
