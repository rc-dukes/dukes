package org.rcsdukes.server;

import static org.junit.Assert.assertNotNull;

import java.io.FileOutputStream;
import java.security.KeyStore;

import org.junit.BeforeClass;
import org.junit.Test;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.net.JksOptions;
import org.rcdukes.common.Config;

/**
 * basic event bus tests see <a href=
 * 'https://github.com/eclipse-vertx/vert.x/blob/master/src/main/java/examples/EventBusExamples.java'>EventBusExamples.java</a>
 * 
 * @author wf
 *
 */
public class TestEventBus {
  @BeforeClass
  public static void configureLogging() {
    Config.configureLogging();
  }

  public void example0_5(Vertx vertx) {
    EventBus eb = vertx.eventBus();
    assertNotNull(eb);
  }

  /**
   * check the start of the clusteredVertex with the given options
   * 
   * @param options
   * @param timeOutMSecs
   * @throws InterruptedException
   */
  public void checkStart(VertxOptions options, int timeOutMSecs)
      throws InterruptedException {
    boolean ready[] = { false };
    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        Vertx vertx = res.result();
        EventBus eventBus = vertx.eventBus();
        System.out.println("We now have a clustered event bus: " + eventBus);
        ready[0] = true;
      } else {
        System.out.println("Failed: " + res.cause());
      }
    });
    int msecs = 0;
    // needs some 4-8 secs on a laptop
    // allow 10x as much for travis
    while (!ready[0] && msecs++ < timeOutMSecs)
      Thread.sleep(1);
    System.out.println(String.format("%s startup after %d msecs",
        ready[0] ? "finished" : "timed out", msecs));

  }

  /**
   * see https://stackoverflow.com/a/13817240/1497139
   * 
   * @param path
   *          - where to create the keystore
   * @throws Exception 
   */
  public void createKeyStore(String path, String pPassword) throws Exception {

    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

    char[] password = pPassword.toCharArray();
    ks.load(null, null);

    // Store away the keystore.
    FileOutputStream fos = new FileOutputStream(path);
    ks.store(fos, password);
    fos.close();
  }

  @Test
  public void testExample13() throws Exception {
    String path="/tmp/keystoreExample13.jks";
    String password="wibble";
    this.createKeyStore(path,password);
    VertxOptions options = new VertxOptions()
        .setEventBusOptions(new EventBusOptions().setSsl(true)
            .setKeyStoreOptions(
                new JksOptions().setPath(path).setPassword(password))
            .setTrustStoreOptions(
                new JksOptions().setPath(path).setPassword(password))
            .setClientAuth(ClientAuth.REQUIRED));
    this.checkStart(options, 80000);
  }

  @Test
  public void testExample14() throws InterruptedException {
    VertxOptions options = new VertxOptions()
        .setEventBusOptions(new EventBusOptions()
            .setClusterPublicHost("whatever").setClusterPublicPort(1234));
    this.checkStart(options, 80000);
  }

}
