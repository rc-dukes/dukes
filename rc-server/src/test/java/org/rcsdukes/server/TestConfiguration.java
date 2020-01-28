package org.rcsdukes.server;

import org.junit.Test;
import org.rcdukes.server.Configuration;

/**
 * test the configuration handling
 * @author wf
 *
 */
public class TestConfiguration {

  @Test
  public void testConfiguration() {
    Configuration config=new Configuration();
    long nodeCount = config.g().V().count().next().longValue();
    System.out.println(nodeCount);
  }
}
