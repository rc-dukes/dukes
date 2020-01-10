package org.rcdukes.app;

import nl.vaneijndhoven.dukes.common.Characters;
import nl.vaneijndhoven.dukes.common.DukesVerticle;

/**
 * JavaFx Application Verticle
 *
 */
public class App extends DukesVerticle {

    public App() {
      super(Characters.UNCLE_JESSE);
    }

    @Override
    public void start() throws Exception {
        super.preStart();
        super.postStart();
    }
}
