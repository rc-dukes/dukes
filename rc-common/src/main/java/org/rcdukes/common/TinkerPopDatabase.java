package org.rcdukes.common;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tinkerpop.gremlin.process.traversal.IO;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * a Gremlin/TinkerPop based in memory database
 * 
 * @author wf
 *
 */
public class TinkerPopDatabase implements GraphDatabase {
  protected static final Logger LOG = LoggerFactory
      .getLogger(TinkerPopDatabase.class);
  protected TinkerGraph graph;
  protected boolean debug=false;

  /**
   * construct me
   */
  public TinkerPopDatabase() {
    graph = TinkerGraph.open();
  }

  /**
   * access to graph
   * 
   * @return the GraphTraversalSource
   */
  public GraphTraversalSource g() {
    return this.graph.traversal();
  }

  /**
   * set a property of the given vertex
   * 
   * @param v
   * @param name
   *          - name of the property
   * @param value
   *          - value of the property
   */
  public void setProp(Vertex v, String name, Object value) {
    if (value != null) {
      v.property(name, value);
    }
  }

  /**
   * write me to the given path
   * 
   * @param path
   */
  public void writeGraph(String path) {
    this.g().io(path).with(IO.writer, IO.graphson).write().iterate();
  }

  /**
   * load the given graphFile
   * 
   * @param graphFile
   */
  public void loadGraph(File graphFile) {
    this.g().io(graphFile.getPath()).with(IO.reader, IO.graphson).read()
        .iterate();
    long nodeCount = this.g().V().count().next().longValue();
    if (debug) {
      String msg = String.format("loaded graph %s with %d nodes",
          graphFile.getPath(), nodeCount);
      LOG.info(msg);
    }
  }

  /**
   * add a vertex for the given object
   * 
   * @param o
   */
  public void addVertex(Object o) {
    Vertex v = this.graph.addVertex(T.label, o.getClass().getSimpleName());
    JsonObject jo = JsonObject.mapFrom(o);
    for (Entry<String, Object> entry : jo.getMap().entrySet()) {
      Object value=entry.getValue();
      if (value!=null)
        v.property(entry.getKey(), value);
    }
  }

  @Override
  public <TYPE> TYPE fromVertex(Vertex v, Class<TYPE> clazz) {
    Map<String, Object> map = new HashMap<String, Object>();
    JsonObject jo = new JsonObject(map);
    for (String key : v.keys()) {
      jo.put(key, v.property(key).value());
    }
    return jo.mapTo(clazz);
  }
}
