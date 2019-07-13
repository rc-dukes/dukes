package nl.vaneijndhoven.dukes.common;

import com.bitplan.error.ErrorHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

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
	 * deploy the given verticles
	 * 
	 * @param verticles
	 */
	public void deployVerticles(AbstractVerticle... verticles) {
		this.clusteredVertx(resultHandler -> {
			vertx = resultHandler.result();
			DeploymentOptions deploymentOptions = new DeploymentOptions();
			deploymentOptions.setWorker(true);
			for (AbstractVerticle verticle : verticles) {
				try {
					vertx.deployVerticle(verticle,deploymentOptions);
				} catch (Throwable th) {
					ErrorHandler.getInstance().handle(th);
				}
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
			options = new VertxOptions().setClustered(true).setClusterManager(Config.createHazelcastConfig());
		}
		return options;
	}
}
