package org.rcdukes.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.junit.Test;

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
    Map<String, Environment> envMap = config.getEnvironments();    
    assertEquals(1,envMap.size());
    Environment env = envMap.values().iterator().next();
    assertEquals("pi.doe.com",env.getString(Config.REMOTECAR_HOST));
    // clean up
    config.getGraphFile().delete();
  }
}
