function StageAssistant() {
}

StageAssistant.prototype.setup = function() {
	var deviceIdAttributes = {
		method: 'getSysProperty',
		parameters: {
			'key': 'com.palm.properties.nduid'
		},
		onSuccess: this.deviceIDServiceRequestHandler.bind(this)
	}
	
	new Mojo.Service.Request('palm://com.palm.preferences/systemProperties', deviceIdAttributes);
};

StageAssistant.prototype.deviceIDServiceRequestHandler = function(resp) {
	var MYNDUID = resp['com.palm.properties.nduid'];
	MYNDUID = MYNDUID[0] + MYNDUID[1] + MYNDUID[2] + MYNDUID[3] + MYNDUID[6] + MYNDUID[9] + MYNDUID[15] + MYNDUID[21];
	
	this.controller.pushScene({name: "main"}, {nduid: MYNDUID});
}