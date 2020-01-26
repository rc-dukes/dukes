package org.rcdukes.server;

import java.io.File;

import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.rcdukes.common.Config;
import org.rcdukes.common.Environment;

/**
 * configuration handler
 * @author wf
 *
 */
public class Configuration {

  
  private TinkerGraph graph;

  public Configuration() {
    Environment env = Config.getEnvironment();
    String configFileName=Environment.dukesHome+"config.json";
    File configFile=new File(configFileName);
    if (configFile.exists()) {
      
    } else {
      graph = TinkerGraph.open();    
    }
  }
}
