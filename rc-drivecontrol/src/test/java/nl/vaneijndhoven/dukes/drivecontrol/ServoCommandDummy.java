package nl.vaneijndhoven.dukes.drivecontrol;

import java.time.Instant;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import nl.vaneijndhoven.dukes.car.ServoCommand;

public class ServoCommandDummy implements ServoCommand {
    // we'll remember the values in an in-memory graph database
    private TinkerGraph graph;
    private long start;
    
    /**
     * create the dummy
     */
    public ServoCommandDummy() {
      graph = TinkerGraph.open();
      // reset the log
      resetLog();
    }
    
    /**
     * get the graph database going
     */
    public void resetLog() {
      start=Instant.now().getNano()/1000000;
    }


    @Override
    public void setServo(int gpioPin, int value) {
      long now = Instant.now().getNano()/1000000;
      // remember the values set with the corresponding time stamps
      graph.addVertex("label", "servo", "gpioPin", gpioPin, "value", value,
          "timestamp", now,"relative",now-start);
      // wait a millisecond since we can't measure time
      // more precisely without effort
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        // ignore
      }
    }
    
    /**
     * get a graph traversal source
     * 
     * @return - the graph traversal source
     */
    public GraphTraversalSource g() {
      return graph.traversal();
    }

    /**
     * show the log of events
     */
    public void showLog() {
      int index[] = { 0 };
      g().V().order().by("timestamp",Order.incr).forEachRemaining(eventV -> {
        System.out.print(String.format("%4d:", ++index[0]));
        for (String key : eventV.keys()) {
          System.out.println(String.format("\t%s=%s", key,eventV.property(key).value().toString()));
        }
      });
    }
  }