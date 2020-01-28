package org.rcsdukes.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.rcdukes.common.Environment;
import org.rcdukes.server.Configuration;

/**
 * test the configuration handling
 * @author wf
 *
 */
public class TestConfiguration {

  @Test
  public void testConfiguration() throws Exception {
    Environment.mock();
    File tempConfig=File.createTempFile("config",".json");
    // make sure the config does not exist so we do not read it
    tempConfig.delete();
    // do not read ini files
    Configuration config=new Configuration(tempConfig.getAbsolutePath(),false);
    // add the mock environment
    config.addEnv(Environment.getInstance());
    long nodeCount = config.g().V().count().next().longValue();
    assertEquals(1,nodeCount);
    config.write();
    assertTrue(tempConfig.canRead());
    // clean up
    tempConfig.delete();
  }
}
