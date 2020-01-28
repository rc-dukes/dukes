export default class Starter {
	constructor(callsign,startMessage,stopMessage,buttonid,publish) {
		this.callsign=callsign;
		this.startMessage=startMessage;
		this.stopMessage=stopMessage;
		this.buttonid=buttonid;
		this.publish=publish;
		this.started=false;
	}
	
	start() {
		this.send(this.startMessage);
		this.setColor(this.buttonid,"red")
		this.started=true;
	}
	
	stop() {
		this.send(this.stopMessage);
		this.setColor(this.buttonid,"blue");
		this.started=true;
	}
	
	toggle() {
		if (!this.started) {
			this.start();
		} else {
			this.stop();	
		}
	}
	
	send(msg) {
	   this.publish(this.callsign + ':'+msg, undefined);
	}
	
	/**
	 * set the color of the element with the given id
	 * 
	 * @param id
	 * @param color
	 */
	setColor(id, color) {
		document.getElementById(id).style.color = color;
	}
}