package nl.vaneijndhoven.dukes.unclejesse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import io.vertx.rxjava.ext.web.handler.sockjs.BridgeOptions;
//import io.vertx.rxjava.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.rxjava.core.AbstractVerticle;

public class UncleJesse extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(UncleJesse.class);

    @Override
    public void start() throws Exception {
        LOG.info("Starting Uncle Jesse ");


        LOG.info("Uncle Jesse started");
    }
}
