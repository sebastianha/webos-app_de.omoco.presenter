function MainAssistant(args) {
	this.nduid = args.nduid;
};

MainAssistant.prototype.setup = function() {
	this.serverurl = "";

	this.spinnerTopLAttrs = {spinnerSize: 'small'};
	this.spinnerTopModel = {spinning: true};
	this.controller.setupWidget('waiting_spinner_top', this.spinnerTopLAttrs, this.spinnerTopModel);

	$("statusoutput").innerHTML = $("statusoutput").innerHTML + "Scanning for Server...<br>";

	this.controller.serviceRequest('palm://com.palm.zeroconf/', {
		method: 'browse',
		parameters:{
			'regType':'_omocops._tcp',
			'subscribe': true
		},
		onSuccess: this.browseSuccess.bind(this),
		onFailure: function(result) {
			Mojo.Log.info("BROWSE failure: " + Object.toJSON(result));
			$("statusoutput").innerHTML = $("statusoutput").innerHTML + "--> ERROR<br>";
		}
	});
};

MainAssistant.prototype.browseSuccess = function(result) {
	Mojo.Log.info("BROWSE success: " + Object.toJSON(result));
	
	if("Add" === result.eventType) {
		Mojo.Log.info("adding service");
		$("statusoutput").innerHTML = $("statusoutput").innerHTML + "--> FOUND!<br>Getting Information...<br>";
		
		var request = new Mojo.Service.Request('palm://com.palm.zeroconf/', {
			method: 'resolve',
			parameters: {
				subscribe: true,
				regType: result.regType,
				domainName: result.domainName,
				instanceName: result.instanceName
			},
			onSuccess: this.resolveSuccess.bind(this),
			onFailure: function(result) {
				Mojo.Log.info("RESOLVE failure: " + Object.toJSON(result)); 
				$("statusoutput").innerHTML = $("statusoutput").innerHTML + "--> ERROR<br>";
			}
		});
	}
};

MainAssistant.prototype.resolveSuccess = function(result) {
	Mojo.Log.info("RESOLVE success: " + Object.toJSON(result));
	if(undefined !== result.targetName) {
		Mojo.Log.info("RESOLVE response: " + result.IPv4Address + " " + result.port);
		$("statusoutput").innerHTML = $("statusoutput").innerHTML + "--> DONE!<br>Authentication...<br><br><b><font color=red>Phone ID: " + this.nduid + "</font></b><br><br>";
		
		this.serverurl = "http://"+result.IPv4Address+":"+result.port+"/";
		var request = new Ajax.Request(this.serverurl + "auth?id=" + this.nduid, {
			method: 'get',
			evalJSON: 'force',
			onSuccess: this.authRequestSuccess.bind(this),
			onFailure: this.authRequestFailure.bind(this)
		});
	}
};

MainAssistant.prototype.authRequestSuccess = function(resp) {
	if(resp.responseJSON.response == "ok") {
		Mojo.Log.info("authRequestSuccess");
		$("statusoutput").innerHTML = $("statusoutput").innerHTML + "--> SUCCESS!<br>Starting Presenter...<br>";
		
		this.spinnerTopModel.spinning = false;
		this.controller.modelChanged(this.spinnerTopModel);
	
		Mojo.Controller.stageController.swapScene({name: "clickp", disableSceneScroller: "true"}, {nduid: this.nduid, serverurl: this.serverurl});
	} else {
		Mojo.Log.error("authRequestFailure");
		$("statusoutput").innerHTML = $("statusoutput").innerHTML + "--> ERROR<br>";
	}
};

MainAssistant.prototype.authRequestFailure = function(resp) {
	Mojo.Log.error("authRequestFailure");
	$("statusoutput").innerHTML = $("statusoutput").innerHTML + "--> ERROR<br>";
};

MainAssistant.prototype.activate = function(event) {
};

MainAssistant.prototype.deactivate = function(event) {
};

MainAssistant.prototype.cleanup = function(event) {
};

MainAssistant.prototype.handleCommand = function(event) {
	if(event.type == Mojo.Event.commandEnable && (event.command == Mojo.Menu.helpCmd)) {
		event.stopPropagation();
	}

	if (event.type == Mojo.Event.command) {
		switch (event.command) {
			case Mojo.Menu.helpCmd:
				Mojo.Controller.stageController.pushAppSupportInfoScene();
				break;
		}
	}
}