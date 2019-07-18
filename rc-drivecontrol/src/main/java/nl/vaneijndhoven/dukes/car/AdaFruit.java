package nl.vaneijndhoven.dukes.car;

import java.math.BigDecimal;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bitplan.error.ErrorHandler;
import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

/**
 * 
 * @author wf set servos using <a href=
 *         "https://github.com/Pi4J/pi4j/blob/master/pi4j-example/src/main/java/PCA9685GpioExample.java">pi4-j
 *         PCA9685 GPIO Example</a>
 * 
 */
public class AdaFruit implements ServoCommand {

	private BigDecimal frequency;
	private I2CBus bus;
	private PCA9685GpioProvider provider;
	private GpioPinPwmOutput[] outputs;
	private static final Logger LOG = LoggerFactory.getLogger(AdaFruit.class);

	/**
	 * default constructor
	 * 
	 * @throws Exception
	 */
	public AdaFruit() throws Exception {
		this(50.0, 1.0);
	}

	/**
	 * construct me
	 * 
	 * @throws Exception
	 */
	public AdaFruit(double pFrequency, double pFrequencyCorrectionFactor)
			throws UnsupportedBusNumberException, Exception {
		// This would theoretically lead into a resolution of 5 microseconds per step:
		// 4096 Steps (12 Bit)
		// T = 4096 * 0.000005s = 0.02048s
		// f = 1 / T = 48.828125
		frequency = new BigDecimal(pFrequency);
		// Correction factor: actualFreq / targetFreq
		// e.g. measured actual frequency is: 51.69 Hz
		// Calculate correction factor: 51.65 / 48.828 = 1.0578
		// --> To measure actual frequency set frequency without correction factor(or
		// set to 1)
		BigDecimal frequencyCorrectionFactor = new BigDecimal(pFrequencyCorrectionFactor);
		// Create custom PCA9685 GPIO provider
		bus = I2CFactory.getInstance(I2CBus.BUS_1);
		provider = new PCA9685GpioProvider(bus, 0x40, frequency, frequencyCorrectionFactor);
		outputs = provisionPwmOutputs(provider);
		// Reset outputs
		provider.reset();
	}

	protected GpioPinPwmOutput[] provisionPwmOutputs(final PCA9685GpioProvider gpioProvider) {
		GpioController gpio = GpioFactory.getInstance();
		GpioPinPwmOutput myOutputs[] = { gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_00, "Pulse 00"),
				gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_01, "Pulse 01"),
				gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_02, "Pulse 02"),
				gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_03, "Pulse 03"),
				gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_04, "Pulse 04"),
				gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_05, "Pulse 05"),
				gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_06, "Pulse 06"),
				gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_07, "Pulse 07"),
				gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_08, "Pulse 08"),
				gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_09, "Pulse 09"),
				gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_10, "Pulse 10"),
				gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_11, "Pulse 11"),
				gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_12, "Pulse 12"),
				gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_13, "Pulse 13"),
				gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_14, "Pulse 14"),
				gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_15, "Pulse 15") };
		return myOutputs;
	}

	private static final int SERVO_DURATION_MIN = 150;
	private static final int SERVO_DURATION_MAX = 600;

	@Override
	public void setServo(int ioId, int value) {
		if ((ioId < 0) || (ioId > 15))
			throw new IllegalArgumentException("Invalied ioId " + ioId + " ioId must be 0-15");
		Pin pin = PCA9685Pin.ALL[ioId];
		int duration = SERVO_DURATION_MIN + value * (SERVO_DURATION_MAX - SERVO_DURATION_MIN) / 256;
		if (debug) {
			String msg = String.format("setting servo %3d to %4d", pin, duration);
			LOG.info(msg);
		}
		provider.setPwm(pin, duration);
	}

	protected CmdLineParser parser;
	@Option(name = "-d", aliases = {
			"--debug" }, usage = "debug\ncreate additional debug output if this switch is used")
	protected boolean debug = false;

	@Option(name = "-i", aliases = { "--ioid" }, usage = "ioid to be used")
	protected int ioId = 0;

	@Option(name = "-v", aliases = { "--value" }, usage = "value to be set")
	protected int value = 0;

	/**
	 * parse the given Arguments
	 * 
	 * @param args
	 * @throws CmdLineException
	 */
	public void parseArguments(String[] args) throws CmdLineException {
		parser = new CmdLineParser(this);
		parser.parseArgument(args);
	}

	public void work() {
		this.setServo(ioId, value);
	}

	/**
	 * command line entry point
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			AdaFruit adaFruit = new AdaFruit();
			adaFruit.parseArguments(args);
			adaFruit.work();
		} catch (Exception e) {
			ErrorHandler.getInstance().handle(e);
		}
	}
}
