package nl.vaneijndhoven.dukes.common;

import io.vertx.core.VertxOptions;

/**
 * starter for VertxCluster
 * 
 * @author wf
 *
 */
public class ClusterStarter {

  private VertxOptions options;

  /**
   * prepare the starter
   */
  public void prepare() {
    Config.configureLogging();
  }

  /**
   * get the VertexOptions
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
