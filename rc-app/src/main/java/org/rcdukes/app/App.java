package org.rcdukes.app;

import org.rcdukes.common.Characters;
import org.rcdukes.common.DukesVerticle;

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
