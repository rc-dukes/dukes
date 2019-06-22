package com.bitplan.rccar.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import nl.vaneijndhoven.dukes.hazardcounty.Environment;

/**
 * test the Environment
 * 
 * @author wf
 *
 */
public class TestEnvironment {

  /**
   * set some properties for test purpose
   * 
   * @param nameValues
   * @return the properties file
   * @throws IOException
   */
  public File setProperties(String... nameValues) throws IOException {
    File propFile = File.createTempFile("testEnvironment", "dukes.ini");
    String props = "# test properties\n";
    for (int i = 0; i < nameValues.length; i += 2) {
      props += nameValues[i] + "=" + nameValues[i + 1] + "\n";
    }
    FileUtils.writeStringToFile(propFile, props, "UTF-8");
    Environment.propFilePath = propFile.getPath();
    // make sure the properties are read again
    Environment.reset();
    return propFile;
  }

  @Test
  public void testEnvironment() throws Exception {
    String ip = "1.2.3.4";
    File propFile = setProperties("targetHost", ip);

    Environment env = Environment.getInstance();
    String piIp = env.getPiAddress();
    // check that the configured ip address is the expected one
    assertEquals(ip, piIp);
    // check that we are not running on a PI - we deploy the code remotely so
    // tests are done on a development machine e.g. laptop
    // comment out if you actually intend to test on the PI
    assertFalse(env.isPi());
    // cleanup
    propFile.delete();
  }

  @Test
  public void testCameraUrl() throws Exception {
    String ip = "5.6.7.8";
    File propFile = setProperties("targetHost", ip);
    assertEquals("http://5.6.7.8/html/cam_pic_new.php",
        Environment.getInstance().getCameraUrl());
    // cleanup
    propFile.delete();
  }

}
