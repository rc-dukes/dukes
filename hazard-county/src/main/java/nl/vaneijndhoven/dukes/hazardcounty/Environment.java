package nl.vaneijndhoven.dukes.hazardcounty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Environment {

    public static final String RASPBERRY_PI_IP = "10.9.8.7";
    private static final Logger LOG = LoggerFactory.getLogger(Environment.class);

    private static Environment instance;
    private boolean runningOnRaspberryPi;

    // do not instantiate
    private Environment() {
        runningOnRaspberryPi = getMyIpAddresses().contains(RASPBERRY_PI_IP);
    }

    public static Environment getInstance() {
        if (instance == null) {
            instance = new Environment();
        }
        return instance;
    }

    public boolean runningOnRaspberryPi() {
        return runningOnRaspberryPi;
    }

    private List<String> getMyIpAddresses() {
        try {
            return Collections.list(NetworkInterface.getNetworkInterfaces())
                    .stream()
                    .map(iface -> Collections.list(iface.getInetAddresses()))
                    .flatMap(Collection::stream)
                    .map(InetAddress::getHostAddress)
                    .collect(Collectors.toList());
        } catch (SocketException e) {
            LOG.error("Error while determining IP addresses: ", e);
            return null;
        }
    }

}
