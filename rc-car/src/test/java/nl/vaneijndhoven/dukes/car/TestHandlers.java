package nl.vaneijndhoven.dukes.car;

import static org.junit.Assert.assertEquals;

import java.time.Instant;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Test;

import io.vertx.core.json.JsonObject;
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
  
  ServoCommandDummy servoCommand;
  
  public Car getCar() throws Exception {
    // Let's fake a car instance with some properties
    Environment.propFilePath = "src/test/resources/dukes/dukes.ini";
    Environment env = Config.getEnvironment();
    assertEquals("pi.doe.com", env.getString(Config.REMOTECAR_HOST));
    // Let's fake a ServoCommand
    servoCommand = new ServoCommandDummy();
    servoCommand.resetLog();
    Thread.sleep(1);
    Car.servoCommand = servoCommand;
    // get a car with the above settings
    Car.resetInstance();
    Car car = Car.getInstance();
    return car;
  }

  @Test
  public void testCar() throws Exception {
    Car car=getCar();
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
  }
  
  @Test
  public void testEngine() throws Exception {
    Car car=getCar();
    Engine engine = car.getEngine();
    engine.forceInNeutral();
    engine.setSpeed(14); // ignored - no power
    car.setPowerOn();
    engine.setSpeed(140);
    if (debug)
      servoCommand.showLog();
  }
  
  @Test  
  public void testSteering() throws Exception {
    Car car=getCar();
    Steering steering = car.getSteering();
    steering.center();
    steering.setWheelPosition(145);
  }
  
  @Test
  public void testLed() throws Exception {  
    Car car=getCar();
    
    Led led = car.getLed();
    led.statusLedOn();
    led.statusLedOff();
    // Let's see what we got up to here ...
    if (debug)
      servoCommand.showLog();
  }
 
  @Test
  public void testSteeringHandler() throws Exception {
    Car car=getCar();
    // make sure commands are accepted
    car.setPowerOn();
    SteeringHandler sh=new SteeringHandler(car);
    Steering steering = car.getSteering();
    JsonObject msg=new JsonObject();
    msg.put("position","center");
    sh.handleServo(msg);
    assertEquals(130,steering.getServo());
    msg.put("position","left");
    sh.handleServo(msg);
    assertEquals(135,steering.getServo());
    msg.put("position","left");
    sh.handleServo(msg);
    assertEquals(140,steering.getServo());
    if (debug)
      servoCommand.showLog();
  }

}
