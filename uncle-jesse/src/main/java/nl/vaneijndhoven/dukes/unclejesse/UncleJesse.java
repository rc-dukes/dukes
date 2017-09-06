package nl.vaneijndhoven.dukes.unclejesse;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
//import io.vertx.rxjava.ext.web.handler.sockjs.BridgeOptions;
//import io.vertx.rxjava.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.rxjava.core.AbstractVerticle;
//import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;
import nl.vaneijndhoven.dukes.hazardcounty.Characters;
import nl.vaneijndhoven.dukes.hazardcounty.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class UncleJesse extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(UncleJesse.class);

    @Override
    public void start() throws Exception {
        LOG.info("Starting Uncle Jesse ");


        LOG.info("Uncle Jesse started");
    }
}
