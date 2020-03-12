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
    assertEquals(1,tpd.g().V().count().next().longValue());
    File gFile = File.createTempFile("gremlin", ".json");
    tpd.writeGraph(gFile.getPath());
    TinkerPopDatabase tpd2=new TinkerPopDatabase();
    tpd2.debug=true;
    tpd2.loadGraph(gFile);
    assertEquals(1,tpd2.g().V().count().next().longValue());
    Vertex markoVertex=tpd2.g().V().has("name","marko").next();
    assertNotNull(markoVertex);
    Person marko=tpd2.fromVertex(markoVertex, Person.class);
    assertNotNull(marko);
    assertEquals("marko",marko.name);
    assertEquals(29,marko.age);
    gFile.delete();
  }

}
