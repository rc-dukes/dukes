package org.rcdukes.common;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

/**
 * test the graph database
 * @author wf
 *
 */
public class TestGraphDatabase {
  /**
   * example class to test object mapping
   * @author wf
   *
   */
  public static class Person {
    public String name;
    public int age;
    public Person() {}
    public Person(String name, int age) {
      super();
      this.name = name;
      this.age = age;
    }
    
  }

  @Test
  public void testTinkerPopDatabase() throws IOException {
    TinkerPopDatabase tpd = new TinkerPopDatabase();
    tpd.addVertex(new Person("marko",29));
    long milliTimeStamp=System.currentTimeMillis();
    tpd.addVertex(new ServoPosition(154,0.5,"m/s","motor"));
    assertEquals(2,tpd.g().V().count().next().longValue());
    File gFile = File.createTempFile("gremlin", ".json");
    tpd.writeGraph(gFile.getPath());
    TinkerPopDatabase tpd2=new TinkerPopDatabase();
    tpd2.debug=true;
    tpd2.loadGraph(gFile);
    assertEquals(2,tpd2.g().V().count().next().longValue());
    Vertex markoVertex=tpd2.g().V().has("name","marko").next();
    assertNotNull(markoVertex);
    Person marko=tpd2.fromVertex(markoVertex, Person.class);
    assertNotNull(marko);
    assertEquals("marko",marko.name);
    assertEquals(29,marko.age);
    Vertex servoPosVertex=tpd2.g().V().hasLabel("ServoPosition").next();
    assertNotNull(servoPosVertex);
    ServoPosition pos2=tpd2.fromVertex(servoPosVertex, ServoPosition.class);
    assertEquals(154,pos2.getServoPos());
    assertEquals(0.5,pos2.getValue(),0.001);
    assertEquals("m/s",pos2.unit);
    assertEquals("motor",pos2.kind);
    assertEquals(milliTimeStamp*1.0,pos2.milliTimeStamp*1.0,2);
    gFile.delete();
  }

}
