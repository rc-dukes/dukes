package org.rcdukes.car;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
//import nl.revolution.dukes.utils.Environment;
//import nl.vaneijndhoven.dukes.generallee.EngineMap;
//import nl.vaneijndhoven.dukes.generallee.SteeringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.rcdukes.error.ErrorHandler;

/**
 * sends commands to the servos and LED via servoblaster
 *
 */
public class ServoBlaster implements ServoCommand {

  private Map<Integer, Integer> mapGpio2Id = new HashMap<Integer, Integer>();
  private static final int SERVOBLASTER_ID_MOTOR = 1; // GPIO-17
  private static final int SERVOBLASTER_ID_WHEEL = 2; // GPIO-18
  private static final int SERVOBLASTER_ID_LED = 6; // GPIO-24

  public static final boolean WRITE_DIRECT = true; // write directly to device

  private static final Logger LOG = LoggerFactory.getLogger(ServoBlaster.class);

  /**
   * construct the servo blaster access
   */
  public ServoBlaster() {
    this.mapGpio2Id.put(17, SERVOBLASTER_ID_MOTOR);
    this.mapGpio2Id.put(18, SERVOBLASTER_ID_WHEEL);
    this.mapGpio2Id.put(24, SERVOBLASTER_ID_LED);
  }
  
  @Override
  public void setServo(int gpioPin, int value) {
    Integer id=this.mapGpio2Id.get(gpioPin);
    if (id==null) {
      LOG.error(String.format("GPIO Pin %d has not ServoBlaster mapping",gpioPin));
      return;
    }
    String servoBlasterCommand = id + "=" + value;
    String msg=String.format("Setting servo on GPIO %d with servoBlaster command: %s",gpioPin,servoBlasterCommand);
    LOG.trace(msg);
    // write the command to the device (if permissions are set correctly)
    File servoBlaster = new File("/dev/servoblaster");
    if (!servoBlaster.canWrite()) {
      LOG.info("/dev/servoblaster can't be written -  not a PI, not installed or permissions missing");
      return;
    }
    try {
      FileUtils.writeStringToFile(servoBlaster, servoBlasterCommand + "\n",
          "UTF-8");
    } catch (IOException e) {
      ErrorHandler.getInstance().handle(e,
          "You might want to check the permissions on "
              + servoBlaster.getAbsolutePath());
    }
  }

}
