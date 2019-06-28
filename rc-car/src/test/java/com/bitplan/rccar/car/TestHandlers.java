package com.bitplan.rccar.car;

import static org.junit.Assert.*;

import java.time.Instant;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Test;

import nl.vaneijndhoven.dukes.car.Engine;
import nl.vaneijndhoven.dukes.car.Led;
import nl.vaneijndhoven.dukes.car.ServoCommand;
import nl.vaneijndhoven.dukes.car.Steering;
import nl.vaneijndhoven.dukes.common.Config;
import nl.vaneijndhoven.dukes.common.Environment;
import nl.vaneijndhoven.dukes.drivecontrol.Car;

/**
 * test the speed and engine handlers
 * 
 * @author wf
 *
 */
public class TestHandlers {

  public static boolean debug=true;
  
  class ServoCommandDummy implements ServoCommand {
    // we'll remember the values in an in-memory graph database
    private TinkerGraph graph;
    private int start;
    
    /**
     * create the dummy
     */
    public ServoCommandDummy() {
      start=Instant.now().getNano()/1000000;
      // reset the log
      resetLog();
    }
    
    /**
     * get the graph database going
     */
    public void resetLog() {
      graph = TinkerGraph.open();
    }


    @Override
    public void setServo(int gpioPin, int value) {
      int now = Instant.now().getNano()/1000000-start;
      // remember the values set with the corresponding time stamps
      graph.addVertex("label", "servo", "gpioPin", gpioPin, "value", value,
          "timestamp", now);
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
      g().V().order(Scope.local).by("timestampe",Order.incr).forEachRemaining(eventV -> {
        System.out.print(String.format("%4d:", ++index[0]));
        for (String key : eventV.keys()) {
          System.out.println(String.format("\t%s=%s", key,eventV.property(key).value().toString()));
        }
      });
    }
  }

  @Test
  public void testCarHandlers() throws Exception {
    // Let's fake a car instance with some properties
    Environment.propFilePath = "src/test/resources/.dukes/dukes.ini";
    Environment env = Config.getEnvironment();
    assertEquals("pi.doe.com", env.getString(Config.REMOTECAR_HOST));
    // Let's fake a ServoCommand
    ServoCommandDummy servoCommand = new ServoCommandDummy();
    Car.servoCommand = servoCommand;
    // get a car with the above settings
    Car car = Car.getInstance();
    // let's play ...
    car.setPowerOn();
    car.turn(-1);
    car.turn(120);
    car.turn(1000);
    car.drive(-1);
    car.drive(150);
    car.drive(1000);
    car.setPowerOff();
    // Let's see what we got up to here ...
    if (debug)
      servoCommand.showLog();
    servoCommand.resetLog();
    Engine engine = car.getEngine();
    engine.forceInNeutral();
    engine.setSpeed(14); // ignored - no power
    car.setPowerOn();
    engine.setSpeed(140);
    Steering steering = car.getSteering();
    steering.center();
    steering.setWheelPosition(145);
    Led led = car.getLed();
    led.statusLedOn();
    led.statusLedOff();
    // Let's see what we got up to here ...
    if (debug)
      servoCommand.showLog();
  }

}
