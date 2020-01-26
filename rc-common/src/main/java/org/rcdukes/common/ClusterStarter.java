package org.rcdukes.common;

import org.rcdukes.error.ErrorHandler;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;

/**
 * starter for VertxCluster
 * 
 * @author wf
 *
 */
public class ClusterStarter {

  private VertxOptions options;
  private boolean prepared = false;
  private boolean debug = false;
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
   * deploy the given array of verticle instances
   * 
   * @param verticles
   * @throws Exception
   */
  public void deployVerticles(AbstractVerticle... verticles) throws Exception {
    DeploymentOptions deploymentOptions = this.getDeployMentOptions(true);
    if (vertx == null) {
      this.clusteredVertx(resultHandler -> {
        vertx = resultHandler.result();
        if (vertx == null) {
          ErrorHandler.getInstance().handle(new RuntimeException(
              "vertx not available resultHandler result is null"));
        }
        this.deployVerticles(deploymentOptions, verticles);
      });
    } else {
      this.deployVerticles(deploymentOptions, verticles);
    }
  }

  /**
   * deploy the given verticles with the given deployment Options
   * @param deploymentOptions
   * @param verticles
   */
  private void deployVerticles(DeploymentOptions deploymentOptions,
      AbstractVerticle[] verticles) {

    for (AbstractVerticle verticle : verticles) {
      try {
        vertx.deployVerticle(verticle, deploymentOptions);
      } catch (Throwable th) {
        ErrorHandler.getInstance().handle(th);
      }
    }
  }

  /**
   * get the VertexOptions
   * 
   * @return new Vertex Options with the HazelcastConfig from Config
   */
  public VertxOptions getOptions() {
    if (options == null) {
      options = new VertxOptions();
      options.getEventBusOptions().setClustered(true);
      options.setClusterManager(Config.createHazelcastConfig());
      // FIXME check necessity of this workaround
      // https://stackoverflow.com/a/30056974/1497139
      // setting check interval to 1 h
      if (debug) {
        options.setBlockedThreadCheckInterval(1000 * 60 * 60);
      } else {
        // error after 5 secs ...
        options.setBlockedThreadCheckInterval(1000 * 5);
      }
    }
    return options;
  }

  /**
   * undeploy the given DukesVerticle
   * @param verticle
   */
  public void undeployVerticle(DukesVerticle verticle) {
    verticle.preStop();
    vertx.undeploy(verticle.deploymentID(),async ->{
      if (async.succeeded()) {
        verticle.postStop();
      }
    });
  }

}
