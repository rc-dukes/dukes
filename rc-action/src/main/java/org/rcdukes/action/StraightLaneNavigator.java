package org.rcdukes.action;

import java.util.List;
import java.util.Locale;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.rcdukes.common.Characters;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.TinkerPopDatabase;
import org.rcdukes.geometry.LaneDetectionResult;
import org.rcdukes.geometry.Line;
import org.rcdukes.video.VideoRecorders.VideoInfo;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.Message;
import stormbots.MiniPID;

/**
 * straight lane navigator
 *
 */
public class StraightLaneNavigator extends TinkerPopDatabase implements Navigator {

  public static int COMMAND_LOOP_INTERVAL = 150; // how often to send commands
  // e.g. 6x per second
  public static final int MAX_DURATION_NO_LINES_DETECTED = 8*COMMAND_LOOP_INTERVAL; // max 1.5
  // secs
  // @TODO
  // should be
  // speed
  // dependent
  // ...
  public static final int DEFAULT_TIME_WINDOW=2*COMMAND_LOOP_INTERVAL;

  protected static final Logger LOG = LoggerFactory
      .getLogger(StraightLaneNavigator.class);
  DukesVerticle sender;
 
  private int timeWindow;
  MiniPID pid;
  private Long tsLatestCommand;
  private boolean emergencyStopActivated = false;
  private long currentTime;
  private Long startTime;
  private AngleRange middleRange;
  private AngleRange stopRangeLeft;
  private AngleRange stopRangeRight;
  private AngleRange leftRange;
  private AngleRange rightRange;
  private AngleRange courseRange;

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
    this(DEFAULT_TIME_WINDOW);
  }

  /**
   * construct me with the given timeWindow
   * @param timeWindow - in milliSeconds
   */
  public StraightLaneNavigator(int timeWindow) {
    this.initDefaults();
    this.timeWindow = timeWindow;
    this.graph.createIndex("frameIndex", Vertex.class);
    this.graph.createIndex("milliTimeStamp", Vertex.class);
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
   * is it o.k to send a command with the given AngleRange?
   * 
   * @param angleRange
   *          - the angle Range to check
   * @param minFound
   * @return true if the angleRange is not null has enough found entries and
   *         enough time has passed since the latest command
   */
  public boolean cmdOk(AngleRange angleRange, int minFound) {
    boolean ok = angleRange != null && angleRange.count > minFound
        && currentTime - tsLatestCommand > COMMAND_LOOP_INTERVAL;
    return ok;
  }

  /**
   * staticical analysis of angles in the given past timeWindow
   * 
   * @author wf
   *
   */
  class AngleRange {
    String name;
    int timeWindow;
    
    int count;
    double sum = 0;
    
    Double min = Double.MAX_VALUE;
    Double max = -Double.MAX_VALUE;
    Double avg = null;
    Double stdDev=null;

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
      if (count == 0) {
        min = null;
        max = null;
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
     * return the value to steer with for this angle range
     * @return - the steering value
     */
    public double steer() {
      double angle = avg;
      // @TODO check polarity and factor
      if (!"course".equals(name)) {
        angle=-0.5*angle;
      }
      return angle;
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
   * analyze the angle ranges int the given timeWindow
   * 
   * @param showDebug
   */
  public void analyzeAngleRanges(int timeWindow, boolean showDebug) {
    long aStart = System.nanoTime();
    middleRange = new AngleRange("middle", currentTime, timeWindow);
    stopRangeLeft = new AngleRange("left", currentTime,
        MAX_DURATION_NO_LINES_DETECTED);
    stopRangeRight = new AngleRange("right", currentTime,
        MAX_DURATION_NO_LINES_DETECTED);
    leftRange = new AngleRange("left", currentTime, timeWindow);
    rightRange = new AngleRange("right", currentTime, timeWindow);
    courseRange = new AngleRange("course", currentTime, timeWindow);
    if (showDebug) {
      long aTime = System.nanoTime()-aStart;
      LOG.info(String.format("analysis took %5.3f msecs", aTime/1000000.0));
      LOG.info(stopRangeLeft.debugInfo());
      LOG.info(stopRangeRight.debugInfo());
      LOG.info(middleRange.debugInfo());
      LOG.info(leftRange.debugInfo());
      LOG.info(rightRange.debugInfo());
      LOG.info(courseRange.debugInfo());
    }
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
    // empty message signals no navigation
    JsonObject message = null;

    // if in emergency stop mode do not continue
    if (this.emergencyStopActivated)
      return message;
    // set the current time and start time
    setTime(ldr);
    // add the lane detection result to the tinker graph
    Vertex ldrVertex=addToGraph(ldr);
    // analyze the LaneDetectionResults of the relevant time Windows
    boolean showDebug = true;
    analyzeAngleRanges(timeWindow, showDebug);

    // do we need to stop since there have been no lines for too long?
    if (currentTime - startTime > MAX_DURATION_NO_LINES_DETECTED
        && stopRangeLeft.count + stopRangeRight.count == 0
        || emergencyStopActivated) {
      this.emergencyStopActivated = true;
      return ActionVerticle.emergencyStopCommand();
    }
    // if we have enough detections for statistics we'll use this information
    int MIN_FOUND_PER_TIMEWINDOW = 3;
    // decide which angleRange to use
    AngleRange navigateRange = null;
    if (middleRange.count >= MIN_FOUND_PER_TIMEWINDOW) {
      navigateRange = courseRange;
    } else {
      // are we left or right heavy?
      navigateRange = leftRange.count > rightRange.count ? leftRange
          : rightRange;
    }
    // check that we can send a command
    if (this.cmdOk(navigateRange, MIN_FOUND_PER_TIMEWINDOW)) {
      double angle = navigateRange.steer();
      String msg = ldr.debugInfo() + String.format("\nsteer by %s: %s",
          navigateRange.name, Line.angleString(angle));
      LOG.debug(msg);
      ldrVertex.property("steer",angle);
      ldrVertex.property("steerBy",navigateRange.name);
      ldrVertex.property("debugInfo",msg);
      tsLatestCommand = currentTime;
      message = steerCommand(angle);
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

  @Override
  public void videoStopped(VideoInfo videoInfo) { 
    addVertex(videoInfo);
    writeGraph(videoInfo.path);
  }

}
