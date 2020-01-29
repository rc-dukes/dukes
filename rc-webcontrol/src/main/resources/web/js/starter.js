// a Starter keeps a state of a verticle and send the given start and
// stop messages when requested via the given publish function
// the button with the given buttonid changes it's color according to the state
export default class Starter {
	// construct me
	constructor(callsign,startMessage,stopMessage,buttonid,publish) {
		this.callsign=callsign;
		this.startMessage=startMessage;
		this.stopMessage=stopMessage;
		this.buttonid=buttonid;
		this.publish=publish;
		this.started=false;
	}
	
	// start me potentially sending a json object
	start(jo=undefined) {
		this.send(this.startMessage,jo);
		this.setColor(this.buttonid,"red")
		this.started=true;
	}
	
	// stop me potentially sending a json object
	stop(jo=undefined) {
		this.send(this.stopMessage,jo);
		this.setColor(this.buttonid,"blue");
		this.started=false;
	}
	
	// toggle my state potentially sending a json object
	toggle(jo=undefined) {
		if (!this.started) {
			this.start(jo);
		} else {
			this.stop(jo);	
		}
	}
	
	// send a message to my callsign potentially with a json object
	send(msg,jo=undefined) {
	   this.publish(this.callsign + ':'+msg,jo);
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