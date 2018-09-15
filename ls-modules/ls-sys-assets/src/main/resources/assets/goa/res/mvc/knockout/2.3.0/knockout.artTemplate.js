ko.artTemplateEngine = function () {  
    window['allowTemplateRewriting'] = false;  
} 
ko.artTemplateEngine.prototype = new ko.templateEngine();
ko.artTemplateEngine.prototype['renderTemplateSource'] = function (templateSource, bindingContext, options) {
	var precompiled = templateSource['data']('precompiled');
	var viewModel = bindingContext['$data'];
    var dataTarget = {};
	var data = ko.mapping.toJS(viewModel);
	if (!precompiled) {
		precompiled = window.template.compile(templateSource.text());
		templateSource['data']('precompiled', precompiled);
	}
	
	// Run the template and parse its output into an array of DOM elements
	var renderedMarkup = precompiled(data);
	return ko.utils.parseHtmlFragment(renderedMarkup);
};

ko.artTemplateEngine.instance = new ko.artTemplateEngine();
ko.setTemplateEngine(ko.artTemplateEngine.instance);
