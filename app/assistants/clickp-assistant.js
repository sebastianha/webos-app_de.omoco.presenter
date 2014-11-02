function ClickpAssistant(args) {
	this.nduid = args.nduid;
	this.serverurl = args.serverurl;
}

ClickpAssistant.prototype.setup = function() {
	this.controller.enableFullScreenMode(true);
	
	this.controller.listen('forward',Mojo.Event.tap,this.sendCommand.bind(this, "34"));
	this.controller.listen('back',Mojo.Event.tap,this.sendCommand.bind(this, "33"));
};

ClickpAssistant.prototype.sendCommand = function(cmd) {
	Mojo.Log.info(Object.toJSON(cmd));
	var request = new Ajax.Request(this.serverurl + "cmd?id=" + this.nduid + "&action=" + cmd, {
		method: 'get',
		evalJSON: 'true',
		onSuccess: this.cmdRequestSuccess.bind(this),
		onFailure: this.cmdRequestFailure.bind(this)
	});
}

ClickpAssistant.prototype.cmdRequestSuccess = function(resp) {
	Mojo.Log.info("cmdRequestSuccess");
};

ClickpAssistant.prototype.cmdRequestFailure = function(resp) {
	Mojo.Log.error("cmdRequestFailure");
};

ClickpAssistant.prototype.activate = function(event) {
	Mojo.Controller.stageController.setWindowProperties({ blockScreenTimeout: true });
};

ClickpAssistant.prototype.deactivate = function(event) {
};

ClickpAssistant.prototype.cleanup = function(event) {
};
