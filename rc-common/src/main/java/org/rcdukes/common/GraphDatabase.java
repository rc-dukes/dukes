package org.rcdukes.common;

import java.io.File;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public interface GraphDatabase {
  /**
   * access the graph database
   * @return the graph traversal source
   */
  public GraphTraversalSource g();
  
  /**
   * write me to the given path
   * @param path
   */
  public void writeGraph(String path);
  
  /**
   * load the given graphFile
   * @param graphFile
   */
  public void loadGraph(File graphFile);
  /**
   * get an object from the given vertex
   * @param v
   * @param clazz
   * @return the object
   */
  public <T> T fromVertex(Vertex v,Class<T> clazz);
  /**
   * add the given object as a vertex
   * @param o
   */
  public void addVertex(Object o);
}
