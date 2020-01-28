package org.rcdukes.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

/**
 * allows to get ip address of raspberry and find out whether we run on a
 * raspberry see
 * <a href='https://stackoverflow.com/a/54304350/1497139'>Stackoverflow
 * question</a> supply service url for camera
 * 
 * @author wf
 *
 */
public class Environment {

	private static final Logger LOG = LoggerFactory.getLogger(Environment.class);
  public static String dukesHome= System.getProperty("user.home") + "/.dukes/";
	public String propFilePath =dukesHome+"dukes.ini";

	private static Environment instance;
	private boolean runningOnRaspberryPi;

	private Properties props;

	/**
	 * read the first line from the given file
	 * 
	 * @param file
	 *            - the file to read
	 * @return the first line
	 */
	public static String readFirstLine(File file) {
		String firstLine = null;
		try {
			if (file.canRead()) {
				FileInputStream fis = new FileInputStream(file);
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
				firstLine = bufferedReader.readLine();
				fis.close();
			}
		} catch (Throwable th) {
			LOG.error(th.getMessage());
			// th.printStackTrace();
		}
		return firstLine;
	}

	/**
	 * get the operating System release
	 * 
	 * @return the first line from /etc/os-release or null
	 */
	public static String osRelease() {
		String os = System.getProperty("os.name");
		if (os.startsWith("Linux")) {
			final File osRelease = new File("/etc", "os-release");
			return readFirstLine(osRelease);
		}
		return null;
	}

	/**
	 * check if this java vm runs on a raspberry PI
	 * https://stackoverflow.com/questions/37053271/the-ideal-way-to-detect-a-raspberry-pi-from-java-jar
	 * 
	 * @return true if this is running on a Raspbian Linux
	 */
	public boolean isPi() {
		String osRelease = osRelease();
		return osRelease != null && osRelease.contains("Raspbian");
	}

	// do not externally instantiate with out params
	private Environment() {
		runningOnRaspberryPi = isPi(); // getMyIpAddresses().contains(RASPBERRY_PI_IP);
	}
	
	/**
	 * configure me from the given propertyFile Path
	 * @param propFilePath
	 */
	public Environment(String propFilePath) {
	  this();
	  this.propFilePath=propFilePath;
	}

	/**
	 * singleton
	 * 
	 * @return the singleton
	 */
	public static Environment getInstance() {
		if (instance == null) {
			instance = new Environment();
		}
		return instance;
	}

	/**
	 * are we running on the raspberry?
	 * 
	 * @return true if so
	 */
	public boolean runningOnRaspberryPi() {
		return runningOnRaspberryPi;
	}

	/**
	 * get the properties from the given path
	 * 
   * @return - the properties
   * @throws Exception
   *             if propfile can't be read
	 */
	public Properties getProperties() throws Exception {
		if (props == null) {
			File propFile = new File(propFilePath);
			if (!propFile.canRead()) {
				throw new Exception("missing property file " + propFile.getAbsolutePath());
			}
			props = new Properties();
			FileInputStream input = new FileInputStream(propFile);
			props.load(new InputStreamReader(input, Charset.forName("UTF-8")));
		}
		return props;
	}
	
	/**
	 * return me as a json object
	 * @return the configuration properties as a single json object
	 * @throws Exception
	 */
	public JsonObject asJsonObject() throws Exception {
		Properties lprops=getProperties();
		JsonObject json = new JsonObject();
		for (Object keyo:lprops.keySet()) {
			String key=keyo.toString();
			json.put(key, lprops.getProperty(key));
		}
		return json;
	}

	/**
	 * get my ip addresses
	 * 
	 * @return - my ip addresses
	 */
	public List<String> getMyIpAddresses() {
		try {
			return Collections.list(NetworkInterface.getNetworkInterfaces()).stream()
					.map(iface -> Collections.list(iface.getInetAddresses())).flatMap(Collection::stream)
					.map(InetAddress::getHostAddress).collect(Collectors.toList());
		} catch (SocketException e) {
			LOG.error("Error while determining IP addresses: ", e);
			return null;
		}
	}

	/**
	 * get the property value for the given key
	 * 
	 * @param key
	 * @return - the string value for the given property key
	 * @throws Exception
	 */
	public String getString(String key) throws Exception {
		Object prop = this.getProperties().get(key);
		if (prop == null)
			throw new IllegalArgumentException("Property " + key + " is not configured in " + propFilePath);
		String value = prop.toString();
		return value;
	}

	/**
	 * get an integer property with the given key
	 * 
	 * @param key
	 * @return - the property value
	 * @throws Exception
	 */
	public int getInteger(String key) throws Exception {
		String valueStr = this.getString(key);
		int value = Integer.parseInt(valueStr);
		return value;
	}

	/**
	 * reset my singleton
	 */
	public static void reset() {
		instance = null;
	}

  public static void mock() {
    // Let's fake a configuration
    Environment.reset();
    Environment env = Environment.getInstance();
    env.propFilePath = "../rc-drivecontrol/src/test/resources/dukes/dukes.ini";
  }

}
