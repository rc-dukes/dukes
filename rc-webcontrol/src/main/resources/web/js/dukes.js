/**
 * rc-dukes webcontrol javascript functions
 */

var eb = null; // we start with an undefined eventbus
var imageViewUrl = 'http://localhost:8081';
var cameraSource = null;
var cameraFps=5;
var recording=false;
var manualwithkeys=false;
var autopiloting=false;
var streetLane = "images/StreetLane.jpg"; // default Image
// 'use strict'
var URL = window.URL || window.webkitURL

/**
 * most publish messages should go thru this function
 * except the high frequency heartbeat
 * @param address
 * @param messageObject
 * @param headers
 */
function publish(address, messageObject, headers) {
  var json=JSON.stringify(messageObject)	
  logMessage(address + "->" + json);
  publishWithOutLog(address,messageObject,headers);
}

/**
 * all publish messages should go thru this function
 * 
 * @param address
 * @param message
 * @param headers
 */
function publishWithOutLog(address,message,headers) {	
	var stateColor = "black";
	if (eb) {
		switch (eb.state) {
		case EventBus.CONNECTING:
			stateColor = "orange";
			break;
		case EventBus.OPEN:
			eb.publish(address, message, headers)
			stateColor = "green";
			break;
		case EventBus.CLOSING:
			stateColor = "orange";
			break;
		case EventBus.CLOSED:
			stateColor = "red";
			break;
		}
	} else {
		stateColor = "violet";
	}
	setColor("events", stateColor);
}

// -1 to scroll
var NUM_LOG_LINES_VISIBLE = -1;
var logLines = [];
var logIndex=0;
/**
 * display events
 */
var display = function(err, msg) {
	var prefix = "";
	if (eb)
		prefix = +eb.state + '/' + EventBus.OPEN + ':';
	var jo=msg.body;
	var show=true;
	if (jo.type==="heartbeat") {
		logIndex++;
		show=logIndex%12==0
	}
	if (show)
		logMessage(prefix + JSON.stringify(jo));
}

/**
 * log the given message
 * 
 * @param msg
 */
function logMessage(msg) {
	var newLine = "<p>\n"; // td
	// var newLine = "\n"; // textarea

	var elem = document.getElementById("events");
	var now = new Date().toISOString();
	var logLine = now + ': ' + msg + newLine;
	logLines.push(logLine);

	if (NUM_LOG_LINES_VISIBLE>0) {
	  if (logLines.length > NUM_LOG_LINES_VISIBLE) {
		logLines = logLines.splice(-NUM_LOG_LINES_VISIBLE);
	  }
	}

	var allLogs = '';
	for (i = 0; i < logLines.length; i++) {
		allLogs += logLines[i];
	}
	elem.innerHTML = allLogs;
	if (NUM_LOG_LINES_VISIBLE<0) {
		elem.scrollTop = elem.scrollHeight;
	}
};

function clearLog(msg) {
	// https://stackoverflow.com/questions/1232040/how-do-i-empty-an-array-in-javascript
	logLines.length = 0;
	logMessage(msg);
}

// automatic repetition of heartbeat
var heartBeatInterval;

/**
 * init remote Screen
 */
function initRemote() {
	initRemoteControls();
}

/**
 * init Detect screen
 */
function initDetect() {
	initRemoteControls();
	initialSliderValues();
	updateImageSources();
}

var powerState = false;

function power() {
	powerState = !powerState;
	setControlState(powerState);
	keyPressed('power');
	if (powerState) {
		clearLog("power on");
		initEventBus(display, true);
		heartBeatInterval = registerHeartBeat();
		updateConfig();
	} else {
		clearLog("power off");
		// switch off heartBeat
		clearInterval(heartBeatInterval);
	}
}

/**
 * set the state of the power - dependent controls
 * 
 * @param powerState
 */
function setControlState(powerState) {
	var color = "grey";
	if (powerState)
		color = "blue";
	setColor("manual", color);
	setColor("autopilot", color);
	setColor("left", color);
	setColor("right", color);
	setColor("up", color);
	setColor("down", color);
	setColor("center", color);
	setColor("stop", color);
	setColor("brake", color);
}

/**
 * init the remote Controls
 */
function initRemoteControls() {
	// redirect console output
	// redirectConsole('console','','\n');

	// allow keyboard input
	registerControls();
	setControlState(false);
}

// TODO decide wether to support SSE
// initialize server side events
function initSSE() {
   var protocol = window.location.protocol;
   if (protocol === "file:") {
	   sseUrl = "http://localhost:8080/configsse"
   } else {
	   sseUrl= window.location.origin + "/configsse"
   }
   eventSource = new EventSource(sseUrl);
   eventSource.onopen = () => {
       logMessage('sse connected...' + '\n');
   };

   eventSource.onmessage = (message) => {
       logMessage("sse:"+message.data + '\n\n');
   };

   eventSource.onerror = () => {
       logMessage('sse error occured...' + '\n');
   };
}

/**
 * init the event Bus
 * 
 * @param withDetect
 */
function initEventBus(withDetect) {
	// https://web-design-weekly.com/snippets/get-url-with-javascript/
	var protocol = window.location.protocol;
	var busUrl = "";
	var msg = window.location.href;
	// if we are called directly from file:// we assume we are in a development
	// environment
	if (protocol === "file:") {
		busUrl = "http://localhost:8080/eventbus";
		imageViewUrl = 'http://localhost:8081';
		// 10 fps
		// debugImagesInterval = registerDebugImages(100);
	} else {
		busUrl = window.location.origin + "/eventbus";
		// TODO port should be configurable
		imageViewUrl = 'http://' + window.location.hostname + ':8081'
		// TODO debug image fps should be configurable
		// here it is 5 fps
		// debugImagesInterval = registerDebugImages(200);
		msg = window.location.origin;
	}
	logMessage("server is " + msg + "\n busUrl=" + busUrl + "\nimageViewUrl="
			+ imageViewUrl);
	eb = new EventBus(busUrl);
	eb.onopen = function() {
		eb.registerHandler("Lost sheep Bo", display); // car
		eb.registerHandler("Lost sheep Luke", display); // action
		eb.registerHandler("Bo Peep", display); // detect
		eb.registerHandler("Shepherd", display); // app
		eb.registerHandler("Red Dog", display); // imageview
		eb.registerHandler("Velvet ears", display); // watchdog
		eb.registerHandler("Little fat buddy", display); // webcontrol
		eb.registerHandler("Crazy Cooter", display); // camera-matrix
		eb.registerHandler("Dipstick", display); // geometry
		eb.registerHandler("Cletus", display); // roi
		logMessage("with no detect finished ...")
		if (withDetect) {
			logMessage("withDetect!");
			// @TODO check
			eb.registerHandler("CANNYCONFIG", display);
			eb.registerHandler("HOUGHLINESCONFIG", display);
			eb.registerHandler("LANEDETECTION", display);
			eb.registerHandler("STARTLIGHTDETECTION", display);
		}
	}
}

function registerHeartBeat() {
	// send a heart beat every 150 millisecs
	return window.setInterval(sendHeartBeat, 150);
}

// register key handling controls
function registerControls() {

	document.onkeydown = function(e) {
		if (!manualwithkeys)
			return;
		e = e || window.event;
		// ignore Command key ⌘
		if (e.metaKey)
			return;
		// see
		// https://hacks.mozilla.org/2017/03/internationalize-your-keyboard-controls/
		// https://www.w3.org/TR/uievents-code/#code-value-tables
		var code = e.charCode;
		var key = e.key;
		if (code == 0) {
			code = e.keyCode;
		}
		console.log('keycode: ', code);
		console.log('key:', key);
		switch (key) {
		case 'ArrowLeft':
		case 'l':
			left(); // wheel left
			break;
		case 'ArrowRight':
		case 'r':
			right(); // wheel right
			break;
		case 'ArrowUp':
		case 'u':
			up(); // speed up
			break;
		case 'ArrowDown':
		case 'd':
			down(); // speed down
			break;
		case 'p':
			power();
			break;
		case ' ': // space
			stop(); // stop
			break;
		case 'b':
		case 's':
			brake(); // brake
			break;
		case 'g':
			// g: go
			sendSpeedDirectCommand("1");
			break;
		case 'z':
		case 'c':
			center();
			break;
		case '+':
			// +: enable autopilot
			autopilot()
			break;
		case '-':
			// -: disable autopilot
			manual();
			break;
		}
		// numeric key
		if (code >= 48 && code <= 58) {
			// 48: 1 key
			// 49: 2 key
			// set speed direct.
			speed = code - 48;
			sendSpeedDirectCommand(speed);
		}
	};
}

// https://stackoverflow.com/a/6604660/1497139
// call e.g. with redirectConsole('debugDiv','<p>','</p>')
function redirectConsole(consoleId, prefix, postfix) {
	if (typeof console != "undefined")
		if (typeof console.log != 'undefined')
			console.olog = console.log;
		else
			console.olog = function() {
			};

	console.log = function(message) {
		console.olog(message);
		document.getElementById(consoleId).innerHTML += prefix + message
				+ postfix;
	};
	console.error = console.debug = console.info = console.log
}

function up() {
	keyPressed("up");
	sendSpeedCommand('up');
}

function down() {
	keyPressed("down");
	sendSpeedCommand('down');
}

function left() {
	keyPressed("left");
	sendWheelCommand('left');
}

function right() {
	keyPressed("right");
	sendWheelCommand('right');
}

function center() {
	// z/c: center wheel
	keyPressed("center");
	sendWheelCommand('center');
}

function stop() {
	keyPressed("stop");
	sendSpeedCommand('stop');
	sendWheelCommand('center');
}

function brake() {
	// b / s: stop (with brake)
	keyPressed("brake");
	sendSpeedCommand('brake');
}

function autopilot() {
	keyPressed("autopilot");
	if (autopiloting)
		stopAutoPilot();
	else
		startAutoPilot();
}

function manual() {
	manualwithkeys=!manualwithkeys;
	if (manualwithkeys) {
		keyPressed("manual");
		stopAutoPilot();
		setColor("manual","red")
	} else {
		setColor("manual","blue")		
	}
}

var CALLSIGN_ROSCO = 'Red Dog';
// record video
function record() {
	if (!recording) {
		publish(CALLSIGN_ROSCO + ':START_RECORDING', undefined);
		setColor("record","red")
	} else {
		publish(CALLSIGN_ROSCO + ':STOP_RECORDING', undefined);
		setColor("record","blue")		
	}
	recording=!recording;
}

function keyPressed(id) {
	console.log(id);
	logMessage(id + " pressed");
	var element = document.getElementById(id);
	element.style.fontWeight = 'bold';
	setTimeout(function() {
		element.style.fontWeight = 'normal';
	}, 200);
}

var CALLSIGN_BO = 'Lost sheep Bo';
function sendWheelCommand(position) {
	data = {
		type : 'servo',
		position : position
	};
	publish(CALLSIGN_BO, data);
}

function sendSpeedCommand(speed) {
	data = {
		type : 'motor',
		speed : speed
	};
	publish(CALLSIGN_BO, data);
}

function sendSpeedDirectCommand(speed) {
	data = {
		type : 'speedDirect',
		speed : '' + speed
	};
	publish(CALLSIGN_BO, data);
}

var CALLSIGN_FLASH = "Velvet ears";
function sendHeartBeat() {
	data = {
		type : 'heartbeat'
	};
	publishWithOutLog(CALLSIGN_FLASH, data);
}

var CALLSIGN_LUKE = "Lost sheep Luke";
function startAutoPilot() {
	publish(CALLSIGN_LUKE + ':START_DRAG_NAVIGATION', undefined);
	setColor("autopilot","red")
	autopiloting=true
}

function stopAutoPilot() {
	publish(CALLSIGN_LUKE + ':STOP_NAVIGATION', undefined);
	setColor("autopilot","blue")
	autopiloting=false;
}

/**
 * set the camera Image Url based on the given base url
 * 
 * @param baseurl
 */
function setCameraImageUrl(baseurl) {
	var url = baseurl; // + '?time=' + new Date().getTime();
	document.getElementById("cameraImage").src = url;
	cameraImageUrlBox.value = url;
}

var CALLSIGN_DAISY = 'Bo Peep';

// update the configuration
function updateConfig() {
	var cameraSource=document.getElementById('cameraSource').value;
	updateImageSources();
	var newCameraFps=document.getElementById('cameraFpsSlider').value;
	var roih=document.getElementById('roihSlider').value;
	var roiw=document.getElementById('roiwSlider').value;
	//if (newCameraFps!=cameraFps)
	//  registerCamera(cameraSource,newCameraFps)
	  
	cameraConfig={}
	cameraConfig.fps=Number(newCameraFps);
	cameraConfig.roih=Number(roih);
	cameraConfig.roiw=Number(roiw);
	cameraConfig.source=cameraSource;
	
	console.log('update camera config',cameraConfig);
	publish(CALLSIGN_DAISY + ':CAMERA_CONFIG_UPDATE', cameraConfig);
	
	var cannyConfigThreshold1 = document
			.getElementById('cannyConfigThreshold1Slider').value;
	var cannyConfigThreshold2 = document
			.getElementById('cannyConfigThreshold2Slider').value;

	cannyConfig = {};
	cannyConfig.threshold1 = Number(cannyConfigThreshold1);
	cannyConfig.threshold2 = Number(cannyConfigThreshold2);

	console.log('update canny config: ', cannyConfig);

	publish(CALLSIGN_DAISY + ':CANNY_CONFIG_UPDATE', cannyConfig);

	var houghConfigRho = document.getElementById('houghConfigRhoSlider').value;
	var houghConfigTheta = document.getElementById('houghConfigThetaSlider').value;
	var houghConfigThreshold = document
			.getElementById('houghConfigThresholdSlider').value;
	var houghConfigMaxLineGap = document
			.getElementById('houghConfigMaxLineGapSlider').value;
	var houghConfigMinLineLength = document
			.getElementById('houghConfigMinLineLengthSlider').value;

	houghConfig = {};
	houghConfig.rho = Number(houghConfigRho);
	houghConfig.theta = Number(houghConfigTheta);
	houghConfig.threshold = Number(houghConfigThreshold);
	houghConfig.maxLineGap = Number(houghConfigMaxLineGap);
	houghConfig.minLineLength = Number(houghConfigMinLineLength);

	console.log('update hough config: ', houghConfig);
	publish(CALLSIGN_DAISY + ':HOUGH_CONFIG_UPDATE', houghConfig);
}

/**
 * set the color of the element with the given id
 * 
 * @param id
 * @param color
 */
function setColor(id, color) {
	document.getElementById(id).style.color = color;
}

/**
 * set the image for the given id to the given source
 * 
 * @param id
 * @param src
 */
function setImage(id, src) {
	// logMessage("id.src:"+id+"->"+src);
	document.getElementById(id).src = src;
}

/**
 * update the textBox for the given textboxId from the slider with the given sliderId
 * 
 * @param sliderId -
 *            the slider to update
 * @param textboxId -
 *            the id of the textbox
 */
function updateTextBoxFromSlider(sliderId, textboxId) {
	var x = document.getElementById(textboxId);
	var y = document.getElementById(sliderId);
	x.value = y.value;
}

// update the given slider and send the new configuration to the server
function onSlide(sliderId, textbox) {
	updateTextBoxFromSlider(sliderId, textbox);
	updateConfig();
}

/**
 * set the initial slider values
 * 
 * @returns
 */
function initialSliderValues() {
	// set initial values for config
	updateTextBoxFromSlider('cameraFpsSlider','cameraFpsTextbox');
	updateTextBoxFromSlider('roihSlider','roihTextbox');
	updateTextBoxFromSlider('roiwSlider','roiwTextbox');
	updateTextBoxFromSlider('cannyConfigThreshold1Slider',
			'cannyConfigThreshold1Textbox');
	updateTextBoxFromSlider('cannyConfigThreshold2Slider',
			'cannyConfigThreshold2Textbox');
	updateTextBoxFromSlider('houghConfigRhoSlider', 'houghConfigRhoTextbox');
	updateTextBoxFromSlider('houghConfigThetaSlider', 'houghConfigThetaTextbox');
	updateTextBoxFromSlider('houghConfigThresholdSlider',
			'houghConfigThresholdTextbox');
	updateTextBoxFromSlider('houghConfigMinLineLengthSlider',
			'houghConfigMinLineLengthTextbox');
	updateTextBoxFromSlider('houghConfigMaxLineGapSlider',
			'houghConfigMaxLineGapTextbox');
	updateTextBoxFromSlider('houghConfigMaxLineGapSlider',
			'houghConfigMaxLineGapTextbox');

	// set initial values for cam config
	updateTextBoxFromSlider('camEcSlider', 'camEcTextbox');
	updateTextBoxFromSlider('camBrSlider', 'camBrTextbox');
	updateTextBoxFromSlider('camSaSlider', 'camSaTextbox');
}

function updateImageSources() {
	setImage("birdseyeImage", imageViewUrl + '?type=birdseye&mode=stream');
	setImage("edgesImage", imageViewUrl + '?type=edges&mode=stream');
	setImage("linesImage", imageViewUrl + '?type=lines&mode=stream');
	setImage("cameraImageDebug", cameraSource);
	setImage("cameraImage", imageViewUrl + '?type=camera&mode=stream');
}

function onSlideCam(sliderId, textbox, configKey) {
	updateTextBoxFromSlider(sliderId, textbox);
	updateCamConfig(sliderId, configKey);
}

// update the camera configuration
function updateCamConfig(elementId, configKey) {
	var configValue = document.getElementById(elementId).value;
	// FIXME - this should not be hardcoded
	var url = 'http://pibeewifi/html/cmd_pipe.php?cmd=' + configKey + '%20'
			+ configValue;

	var ajax_cmd = new XMLHttpRequest();
	ajax_cmd.open("GET", url, true);
	ajax_cmd.send();
}
