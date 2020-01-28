package org.rcdukes.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.rcdukes.common.Configuration;
import org.rcdukes.common.Environment;

/**
 * test the configuration handling
 * @author wf
 *
 */
public class TestConfiguration {
  
  public Configuration getMockConfiguration() throws Exception {
    Environment.mock();
    File tempConfig=File.createTempFile("config",".json");
    // make sure the config does not exist so we do not read it
    tempConfig.delete();
    // do not read ini files
    Configuration config=new Configuration(tempConfig.getAbsolutePath(),false);
    // add the mock environment
    config.addEnv(Environment.getInstance());
    return config;
  }

  @Test
  public void testConfiguration() throws Exception {
    Configuration config=getMockConfiguration();
    long nodeCount = config.g().V().count().next().longValue();
    assertEquals(1,nodeCount);
    config.write();
    assertTrue(config.getGraphFile().canRead());
    // clean up
    config.getGraphFile().delete();
  }
  
  @Test
  public void testQuery() throws Exception {
    Configuration config=getMockConfiguration();
    config.write();
    System.out.println(config.asString());
    // clean up
    config.getGraphFile().delete();
  }
}
