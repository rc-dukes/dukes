package nl.vaneijndhoven.dukes.common;
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
}
