package org.rcdukes.common;

import java.io.File;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.tinkerpop.gremlin.process.traversal.IO;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

/**
 * configuration handler
 * 
 * @author wf
 *
 */
public class Configuration {

  private TinkerGraph graph;

  public static String STORE_MODE = IO.graphson;
  public static String STORE_EXTENSION = ".json";
  private String graphFilePath;

  /**
   * @return the graphFilePath
   */
  public String getGraphFilePath() {
    return graphFilePath;
  }

  /**
   * @param graphFilePath
   *          the graphFilePath to set
   */
  public void setGraphFilePath(String graphFilePath) {
    this.graphFilePath = graphFilePath;
  }

  /**
   * construct me
   */
  public Configuration(String graphFilePath, boolean readInis) {
    this.graphFilePath = graphFilePath;
    setGraph(TinkerGraph.open());
    File graphFile = getGraphFile();
    if (readInis) {
      fromIni();
      write();
    } else {
      if (graphFile.exists()) {
        read(graphFile);
      }
    }
  }

  /**
   * construct me
   */
  public Configuration() {
    this(Environment.dukesHome + "config" + STORE_EXTENSION,true);
  }

  /**
   * get my values from the "*.ini" property files in dukesHome
   */
  public void fromIni() {
    File dukesHome = new File(Environment.dukesHome);
    String[] homeFiles = dukesHome.list();
    for (String homeFile : homeFiles) {
      if (homeFile.endsWith(".ini")) {
        String iniPath = dukesHome + "/" + homeFile;
        Environment env = new Environment(iniPath);
        this.addEnv(env);
      }
    }
  }

  public void addEnv(Environment env) {
    try {
      Vertex v = graph.addVertex("config");
      v.property("inipath", env.propFilePath);
      Properties lprops = env.getProperties();
      for (Object keyo : lprops.keySet()) {
        String key = keyo.toString();
        String value = lprops.getProperty(key);
        v.property(key, value);
      }
    } catch (Exception e) {
      // bad luck
    }
  }

  public TinkerGraph getGraph() {
    return graph;
  }

  public void setGraph(TinkerGraph graph) {
    this.graph = graph;
  }

  public GraphTraversalSource g() {
    return graph.traversal();
  }

  /**
   * get the GraphFile
   * 
   * @return - the GraphFile
   */
  public File getGraphFile() {
    File graphFile = new File(getGraphFilePath());
    return graphFile;
  }

  /**
   * read my data from the given graphFile
   * 
   * @param graphFile
   */
  public void read(File graphFile) {
    // http://tinkerpop.apache.org/docs/3.4.0/reference/#io-step
    getGraph().traversal().io(graphFile.getPath()).with(IO.reader, STORE_MODE)
        .read().iterate();
  }

  /**
   * write my data to the given graphFile
   * 
   * @param graphFile
   */
  public void write(File graphFile) {
    // http://tinkerpop.apache.org/docs/3.4.0/reference/#io-step
    getGraph().traversal().io(graphFile.getPath()).with(IO.writer, STORE_MODE)
        .write().iterate();
  }
  
  public String asString() throws Exception {
    String text=FileUtils.readFileToString(getGraphFile(), "utf-8");
    return text;
  }

  /**
   * write me
   */
  public void write() {
    File graphFile = getGraphFile();
    if (!graphFile.getParentFile().exists()) {
      graphFile.getParentFile().mkdirs();
    }
    write(graphFile);
  }
}
