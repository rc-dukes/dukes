package org.rcdukes.action;

import org.rcdukes.common.DukesVerticle;
import org.rcdukes.common.GraphDatabase;
import org.rcdukes.geometry.LaneDetectionResult;
import org.rcdukes.video.VideoRecorders.VideoInfo;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;

/**
 * navigation interface
 * @author wf
 *
 */
public interface Navigator extends GraphDatabase {
  
  public DukesVerticle getSender();
  public void setSender(DukesVerticle sender);
  /**
   * convert a lane detection result Json Object to a LaneDetectionResult
   * @param ldrJo
   * @return the lane detection result
   */
  public LaneDetectionResult fromJsonObject(JsonObject ldrJo);
  /** 
   * get a navigation instruction
   * based on the given LaneDetectionResult 
   */
  public JsonObject getNavigationInstruction(LaneDetectionResult ldr);
  /**
   * navigate based on the given instruction
   * @param navigationInstruction
   */ 
  public void navigateWithInstruction(JsonObject navigationInstruction);
  
  /**
   * navigate with the given vert.x eventbus message
   * @param ldrMessage
   */
  public void navigateWithMessage(Message<JsonObject> ldrMessage);
  
  /**
   * navigate with the given LaneDetectionResult
   * @param ldr
   */
  public void navigateWithLaneDetectionResult(LaneDetectionResult ldr);
  
  /**
   * handle a stopped video
   * @param videoInfo
   */
  public void videoStopped(VideoInfo videoInfo);
 
}
