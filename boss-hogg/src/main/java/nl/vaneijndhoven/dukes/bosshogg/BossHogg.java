package nl.vaneijndhoven.dukes.bosshogg;

import com.hazelcast.config.Config;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import nl.vaneijndhoven.dukes.common.Characters;
import nl.vaneijndhoven.dukes.unclejesse.UncleJesse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscription;

public class BossHogg extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(BossHogg.class);


    @Override
    public void start() throws Exception {
        LOG.info("Starting Boss Hogg (manual UI controller");

        super.start();

        Router router = Router.router(vertx);
        BridgeOptions options = new BridgeOptions();

//        Stream.of(Characters.values()).forEach(character -> {
//            PermittedOptions permitted = new PermittedOptions().setAddress(character.getCallsign());
//            options.addOutboundPermitted(permitted);
//            options.addInboundPermitted(permitted);
//
//        });
//        Stream.of(Events.values()).forEach(event -> {
//            options.addOutboundPermitted(new PermittedOptions().setAddress(event.name()))
//        });

        options.addOutboundPermitted(new PermittedOptions().setAddressRegex(".*"));
        options.addInboundPermitted(new PermittedOptions().setAddressRegex(".*"));
        router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(options));
        router.route().handler(StaticHandler.create("web").setCachingEnabled(false));
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);

//        vertx.eventBus().sendObservable(Characters.DAISY.getCallsign() + "lane.start", new JsonObject().put("source", "file://Users/jpoint/Repositories/opencv-playground/src/main/resources/videos/full_run.mp4"));


        LOG.info("Boss Hogg started");
    }



}
