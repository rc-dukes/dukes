/**
 * rc-dukes webcontrol javascript functions
 */
// redirect console output
// redirectConsole('console','','\n');
// allow keyboard input
function initPage() {
	registerControls();

	var eb = new EventBus("http://localhost:8080/eventbus");

	var NUM_LOG_LINES_VISIBLE = 15;
	var logLines = [];

	var display = function(err, msg) {
		var elem = document.getElementById("events");
		var logLine = new Date() + ' ' + JSON.stringify(msg.body);
		logLines.push(logLine);

		if (logLines.length > NUM_LOG_LINES_VISIBLE) {
			logLines = logLines.splice(-NUM_LOG_LINES_VISIBLE);
		}

		var allLogs = '';
		for (i = 0; i < logLines.length; i++) {
			allLogs += '\n' + logLines[i];
		}
		elem.innerText = allLogs;
	};

	eb.onopen = function() {
		eb.registerHandler("Lost sheep Bo", display);
		eb.registerHandler("Lost sheep Luke", display);
		eb.registerHandler("Bo Peep", display);
		eb.registerHandler("Shepherd", display);
		eb.registerHandler("Red Dog", display);
		// eb.registerHandler("Velvet ears", display);
		eb.registerHandler("Little fat buddy", display);
		eb.registerHandler("Crazy Cooter", display);
		eb.registerHandler("Dipstick", display);
		eb.registerHandler("Cletus", display);
		eb.registerHandler("STREAMADDED", function(err, msg) {
			var videoNode = document.querySelector('video');
			videoNode.src = "blob:" + msg.body.source;
		});
		eb.registerHandler("CANNYCONFIG", display);
		eb.registerHandler("HOUGHLINESCONFIG", display);
		eb.registerHandler("LANEDETECTION", display);
		eb.registerHandler("STARTLIGHTDETECTION", display);

		registerHeartBeat();
		registerDebugImages();
	}

	// function localFileVideoPlayer() {
	// 'use strict'
	var URL = window.URL || window.webkitURL
	var displayMessage = function(message, isError) {
		var element = document.querySelector('#message')
		element.innerHTML = message;
		element.className = isError ? 'error' : 'info'
	};

	var playSelectedFile = function(event) {
		var file = this.files[0]
		var type = file.type
		var videoNode = document.querySelector('video')
		var canPlay = videoNode.canPlayType(type)
		if (canPlay === '')
			canPlay = 'no'
		var message = 'Can play type "' + type + '": ' + canPlay
		var isError = canPlay === 'no'
		displayMessage(message, isError)

		if (isError) {
			return

			

					

			

									

			

					

			

		}

		var fReader = new FileReader();
		fReader.readAsDataURL(file);
		fReader.onloadend = function(event) {

			var fileURL = URL.createObjectURL(file)
			eb.publish("STREAMADDED", {
				"source" : fileURL.replace(/blob:/, ''),
				"config" : {
					"interval" : 100
				}
			});
		};

		var inputNode = document.querySelector('input')

		inputNode.addEventListener('change', playSelectedFile, false)
	}
}

function registerHeartBeat() {
	window.setInterval(sendHeartBeat, 150);
}

// register key handling controls
function registerControls() {

	document.onkeydown = function(e) {
		e = e || window.event;

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
	startAutoPilot();
}

function manual() {
	keyPressed("manual");
	stopAutoPilot();
}

function keyPressed(id) {
	console.log(id);
	var element = document.getElementById(id);
	element.style.fontWeight = 'bold';
	setTimeout(function() {
		element.style.fontWeight = 'normal';
	}, 200);
}

var CALLSIGN_BO = 'Lost sheep Bo';
var CALLSIGN_DAISY = 'Bo Peep';

function sendWheelCommand(position) {

	data = {
		type : 'servo',
		position : position
	};
	eb.publish(CALLSIGN_BO, data);
}

function sendSpeedCommand(speed) {
	data = {
		type : 'motor',
		speed : speed
	};
	eb.publish(CALLSIGN_BO, data);
}

function sendSpeedDirectCommand(speed) {
	data = {
		type : 'speedDirect',
		speed : '' + speed
	};
	eb.publish(CALLSIGN_BO, data);
}

var CALLSIGN_FLASH = "Velvet ears";
function sendHeartBeat() {
	data = {
		type : 'heartbeat'
	};
	eb.publish(CALLSIGN_FLASH, data);
}

var CALLSIGN_LUKE = "Lost sheep Luke";
function startAutoPilot() {
	eb.publish(CALLSIGN_LUKE + ':START_DRAG_NAVIGATION', undefined);
}

function stopAutoPilot() {
	eb.publish(CALLSIGN_LUKE + ':STOP_NAVIGATION', undefined);
}

function registerDebugImages() {
	window.setInterval(updateDebugImages, 100);
}

function updateDebugImages() {
	document.getElementById("originalImage").src = 'http://pibeewifi/html/cam_pic.php'; // 'http://localhost:8081?type=original&'
	// +
	// Math.random();
	document.getElementById("birdseyeImage").src = 'http://localhost:8081?type=birdseye&'
			+ Math.random();
	document.getElementById("edgesImage").src = 'http://localhost:8081?type=edges&'
			+ Math.random();
	document.getElementById("linesImage").src = 'http://localhost:8081?type=lines'
			+ Math.random();
}

function updateConfig() {
	var cannyConfigThreshold1 = document
			.getElementById('cannyConfigThreshold1Slider').value;
	var cannyConfigThreshold2 = document
			.getElementById('cannyConfigThreshold2Slider').value;

	cannyConfig = {};
	cannyConfig.threshold1 = Number(cannyConfigThreshold1);
	cannyConfig.threshold2 = Number(cannyConfigThreshold2);

	console.log('update canny config: ', cannyConfig);

	eb.publish(CALLSIGN_DAISY + ':CANNY_CONFIG_UPDATE', cannyConfig);

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
	eb.publish(CALLSIGN_DAISY + ':HOUGH_CONFIG_UPDATE', houghConfig);
}

function updateSliderValue(sliderId, textbox) {
	var x = document.getElementById(textbox);
	var y = document.getElementById(sliderId);
	x.value = y.value;
}

function onSlide(sliderId, textbox) {
	updateSliderValue(sliderId, textbox);
	updateConfig();
}

// set initial values for config
updateSliderValue('cannyConfigThreshold1Slider', 'cannyConfigThreshold1Textbox');
updateSliderValue('cannyConfigThreshold2Slider', 'cannyConfigThreshold2Textbox');
updateSliderValue('houghConfigRhoSlider', 'houghConfigRhoTextbox');
updateSliderValue('houghConfigThetaSlider', 'houghConfigThetaTextbox');
updateSliderValue('houghConfigThresholdSlider', 'houghConfigThresholdTextbox');
updateSliderValue('houghConfigMinLineLengthSlider',
		'houghConfigMinLineLengthTextbox');
updateSliderValue('houghConfigMaxLineGapSlider', 'houghConfigMaxLineGapTextbox');
updateSliderValue('houghConfigMaxLineGapSlider', 'houghConfigMaxLineGapTextbox');

// set initial values for cam config
updateSliderValue('camEcSlider', 'camEcTextbox');
updateSliderValue('camBrSlider', 'camBrTextbox');
updateSliderValue('camSaSlider', 'camSaTextbox');

function onSlideCam(sliderId, textbox, configKey) {
	updateSliderValue(sliderId, textbox);
	updateCamConfig(sliderId, configKey);
}

function updateCamConfig(elementId, configKey) {
	var configValue = document.getElementById(elementId).value;
	var url = 'http://10.9.8.7/html/cmd_pipe.php?cmd=' + configKey + '%20'
			+ configValue;

	var ajax_cmd = new XMLHttpRequest();
	ajax_cmd.open("GET", url, true);
	ajax_cmd.send();
}

// setInterval(function() {
// document.getElementById("cameraImage").src='http://10.9.8.7/html/cam_get.php?timestamp='
// + new Date();
// }, 50);
