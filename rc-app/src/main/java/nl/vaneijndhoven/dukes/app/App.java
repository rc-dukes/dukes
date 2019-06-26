package nl.vaneijndhoven.dukes.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import io.vertx.rxjava.ext.web.handler.sockjs.BridgeOptions;
//import io.vertx.rxjava.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.rxjava.core.AbstractVerticle;

/**
 * JavaFx Application Verticle
 *
 */
public class App extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    @Override
    public void start() throws Exception {
        LOG.info("Starting App Uncle Jesse ");


        LOG.info("App Uncle Jesse started");
    }
}
