function MainManualAssistant() {
}

MainManualAssistant.prototype.setup = function() {
	var nameattr = {
		hintText: 'Server IP...',
		textFieldName: 'name', 
		modelProperty: 'original', 
		multiline: false,
		focus: true, 
		maxLength: 50,
	};
	namemodel = {
		'original' : "",
		disabled: false
	};
	this.controller.setupWidget('ip', nameattr, namemodel);
	
	this.controller.listen($('connect'),Mojo.Event.tap, this.connect.bind(this));
};

MainManualAssistant.prototype.connect = function(event) {
	Mojo.Controller.stageController.swapScene({name: "clickp", disableSceneScroller: "true"}, {nduid: this.nduid, serverurl: namemodel['original']});
};

MainManualAssistant.prototype.activate = function(event) {
};

MainManualAssistant.prototype.deactivate = function(event) {
};

MainManualAssistant.prototype.cleanup = function(event) {
};
