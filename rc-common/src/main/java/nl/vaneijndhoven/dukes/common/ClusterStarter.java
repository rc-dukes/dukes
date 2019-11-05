package nl.vaneijndhoven.dukes.common;

import com.bitplan.error.ErrorHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

/**
 * starter for VertxCluster
 * 
 * @author wf
 *
 */
public class ClusterStarter {

  private VertxOptions options;
  private boolean prepared = false;
  private Vertx vertx;

  public Vertx getVertx() {
    return vertx;
  }

  /**
   * prepare the starter
   */
  public void prepare() {
    if (!prepared) {
      Config.configureLogging();
      prepared = true;
    }
  }

  /**
   * 
   * @param resultHandler
   */
  public void clusteredVertx(Handler<AsyncResult<Vertx>> resultHandler) {
    prepare();
    Vertx.clusteredVertx(getOptions(), resultHandler);
  }

  /**
   * get the deployment options
   * 
   * @param worker
   *          - true if worker should be set to true
   * @return the deployment options
   * @throws Exception
   */
  public DeploymentOptions getDeployMentOptions(boolean worker)
      throws Exception {
    DeploymentOptions deploymentOptions = new DeploymentOptions();
    deploymentOptions.setWorker(worker);
    JsonObject config = Config.getEnvironment().asJsonObject();
    deploymentOptions.setConfig(config);
    return deploymentOptions;
  }

  /**
   * deploy the given verticles
   * 
   * @param verticles
   * @throws Exception
   */
  public void deployVerticles(AbstractVerticle... verticles) throws Exception {
    DeploymentOptions deploymentOptions = this.getDeployMentOptions(true);
    this.clusteredVertx(resultHandler -> {
      vertx = resultHandler.result();
      if (vertx != null) {
        for (AbstractVerticle verticle : verticles) {
          try {
            vertx.deployVerticle(verticle, deploymentOptions);
          } catch (Throwable th) {
            ErrorHandler.getInstance().handle(th);
          }
        }
      } else {
        ErrorHandler.getInstance().handle(new RuntimeException("vertx not available resultHandler result is null"));
      }
    });
  }

  /**
   * get the VertexOptions
   * 
   * @return new Vertex Options with the HazelcastConfig from Config
   */
  public VertxOptions getOptions() {
    if (options == null) {
      options = new VertxOptions().setClustered(true)
          .setClusterManager(Config.createHazelcastConfig());
    }
    return options;
  }
}
