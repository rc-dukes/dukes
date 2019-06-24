package com.bitplan.rc.common;

import static org.junit.Assert.*;

import org.junit.Test;

import nl.vaneijndhoven.dukes.common.ClusterStarter;

/**
 * test the ClusterStarter
 * @author wf
 *
 */
public class TestClusterStarter {

  @Test
  public void testClusterStarter() {
    ClusterStarter starter = new ClusterStarter();
    starter.prepare();
  }

}
