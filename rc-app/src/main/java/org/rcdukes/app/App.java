package org.rcdukes.app;

import org.rcdukes.common.Characters;
import org.rcdukes.common.ClusterStarter;
import org.rcdukes.common.DukesVerticle;
import org.rcdukes.error.ErrorHandler;

import io.vertx.core.json.JsonObject;

/**
 * JavaFx Application Verticle
 *
 */
public class App extends DukesVerticle {

  private static App instance;
  private ClusterStarter starter;

  public App() {
    super(Characters.UNCLE_JESSE);
  }

  @Override
  public void start() throws Exception {
    super.preStart();
    super.postStart();
  }

  /**
   * send the given speed command
   * 
   * @param speed
   */
  public void sendSpeedCommand(String speed) {
    JsonObject jo = new JsonObject();
    jo.put("type", "motor");
    jo.put("speed", "speed");
    super.send(Characters.BO, jo);
  }

  public void startUp() {
    starter = new ClusterStarter();
    starter.prepare();
    // bootstrap the deployment by deploying me
    try {
      starter.deployVerticles(this);
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }
  }
  
  public static App getInstance() {
    instance=new App();
    instance.startUp();
    return instance;
  }

  public void heartBeat(boolean on) {
    
    
  }
}
