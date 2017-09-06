package nl.vaneijndhoven.dukes.car;

//import nl.revolution.dukes.utils.Environment;
//import nl.vaneijndhoven.dukes.generallee.EngineMap;
//import nl.vaneijndhoven.dukes.generallee.SteeringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Command {

    private static final int LED_ON = 250;
    private static final int LED_OFF = 0;
    private static final int SERVOBLASTER_ID_MOTOR = 1; // GPIO-17
    private static final int SERVOBLASTER_ID_WHEEL = 2; // GPIO-18
    private static final int SERVOBLASTER_ID_LED = 6; // GPIO-24

    private static final Logger LOG = LoggerFactory.getLogger(Command.class);

    private static boolean powerIsOn = false;

    private Command() {
        // don't instantiate, use static access
    }

//    public static void setMotorSpeed(int speed, boolean force) {
//        if (!Command.powerIsOn() && force == false) {
//            LOG.debug("Not setting motor value; power is off.");
//            return;
//        }
//
//        LOG.debug("Setting motor to value " + speed);
//        servoBlaster(SERVOBLASTER_ID_MOTOR, speed);
//    }
//
//    public static void setWheelPosition(int position, boolean force) {
//        if (!Command.powerIsOn() && force == false) {
//            LOG.debug("Not setting servo value; power is off.");
//            return;
//        }
//
//        LOG.debug("Setting servo to value " + position);
//        servoBlaster(SERVOBLASTER_ID_WHEEL, position);
//    }

//    public static void stop() {
//        servoBlaster(SERVOBLASTER_ID_MOTOR, EngineMap.SPEED_ZERO);
//        CoreController.setCurrentSpeedToZero();
//        servoBlaster(SERVOBLASTER_ID_WHEEL, SteeringMap.WHEEL_CENTER);
//    }

    public static void statusLedOn() {
        LOG.debug("Setting status led ON");
        servoBlaster(SERVOBLASTER_ID_LED, LED_ON);
    }

    public static void statusLedOff() {
        LOG.debug("Setting status led OFF");
        servoBlaster(SERVOBLASTER_ID_LED, LED_OFF);
    }

    public static void setPowerOn() {
        if (!powerIsOn) {
            LOG.info("Setting power ON");
            powerIsOn = true;
            statusLedOn();
        }
    }

    public static void setPowerOff() {
        if (powerIsOn) {
            LOG.info("Setting power OFF");
//            stop();
            powerIsOn = false;
            statusLedOff();
        }
    }

    public static boolean powerIsOn() {
        return powerIsOn;
    }

    public static void servoBlaster(int id, int value) {
        String servoBlasterCommand = id + "=" + value;
        LOG.trace("Sending to ServoBlaster: " + servoBlasterCommand);
        String shellCommand = "echo \"" + servoBlasterCommand + "\">/dev/servoblaster";
        execShellCommand(shellCommand);
    }

    private static void execShellCommand(String command) {
//        if (!Environment.getInstance().runningOnRaspberryPi()) {
//            LOG.trace("Not running on the Raspberry Pi - not executing command: " + command);
//            return;
//        }

        LOG.trace("executing command: " + command);
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
            p.waitFor();

            String output = inputStreamToString(p.getInputStream());
            if (output != null) {
                LOG.debug("command output: " + output);
            }

            String error = inputStreamToString(p.getErrorStream());
            if (error != null) {
                LOG.error("command error: " + error);
            }
        } catch (IOException | InterruptedException e) {
            LOG.error("Error executing command: ", e);
        }
    }

    private static String inputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine())!= null) {
            output.append(line).append("\n");
        }
        if (output.length() == 0) {
            return null;
        }
        return output.toString();
    }

}
