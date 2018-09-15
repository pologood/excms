<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String rootPath = "/WEB-INF/jsp/process/diagram/";
%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>查看流程图</title>
<link rel="stylesheet" href="/assets/res/css/common.css">
    <link rel="stylesheet"  href="<%=rootPath%>styles/grapheditor.css?v=11">
</head>
<body>

	<div style="position: absolute;left:460px;top:5px;">
		<table style="height:50px;width:120px;">
			<tr>
				<td>
					<div style="width: 20px;height: 20px;background-color: #00FF00;"></div>
				</td>
				<td>
					参与的步骤
				</td>
			</tr>
				<tr>
					<td><div style="width: 20px;height: 20px;background-color: #ff3333;"></div></td>
					<td>当前步骤</td>
				</tr>
		</table>
	</div>

<script type="text/javascript">
	var urlParams = (function(url){
			var result = new Object();
			var idx = url.lastIndexOf('?');	
			if (idx > 0){
				var params = url.substring(idx + 1).split('&');				
				for (var i = 0; i < params.length; i++){
					idx = params[i].indexOf('=');
					if (idx > 0){
						result[params[i].substring(0, idx)] = params[i].substring(idx + 1);
					}
				}
			}			
			return result;
		})(window.location.href);

	var MAX_REQUEST_SIZE = 10485760;
	var MAX_WIDTH = 6000;
	var MAX_HEIGHT = 6000;
	
	var RESOURCES_PATH = '<%=rootPath%>resources';
	var RESOURCE_BASE = RESOURCES_PATH + '/grapheditor';
	var STENCIL_PATH = '<%=rootPath%>stencils';
	var IMAGE_PATH = '<%=rootPath%>images';
	var STYLE_PATH = '<%=rootPath%>styles';
	var CSS_PATH = '<%=rootPath%>styles';
	var OPEN_FORM = '/bpm_designer/diagram/jump';
	var SELECT_MODEL = null;

	var mxBasePath = '<%=rootPath%>src';
	var mxLanguage = urlParams['lang'];
	var mxLanguages = ['zh'];

	var DIAGRAM_PATH = '/customForm/process/getDiagram';
	var tapAndHoldStartsConnection = true;
	var showConnectorImg = true;
	var mxLoadResources = false;
	var urlParams = (function(url)
	{
		var result = new Object();
		var idx = url.lastIndexOf('?');

		if (idx > 0)
		{
			var params = url.substring(idx + 1).split('&');
			
			for (var i = 0; i < params.length; i++)
			{
				idx = params[i].indexOf('=');
				
				if (idx > 0)
				{
					result[params[i].substring(0, idx)] = params[i].substring(idx + 1);
				}
			}
		}
		
		return result;
	})(window.location.href);	
	
	
	
	
	
</script>
<script src="/assets/core/jquery.min.js"></script>
 	<script src="/assets/core/boot.js" debug="1"></script>   
	<script src="<%=rootPath%>src/js/mxClient.js"></script>
	<script src="<%=rootPath%>js/flowDesign.js?v=12"></script>
	<script src="<%=rootPath%>js/Sidebar.js?v=12"></script>
	<script src="<%=rootPath%>js/Shapes.js?v=12"></script>
	<script src="<%=rootPath%>js/Menus.js?v=12"></script>
	<script src="<%=rootPath%>js/Actions.js?v=12"></script>
	<script src="<%=rootPath%>js/Dialogs.js?v=12"></script>
	<script src="<%=rootPath%>js/Editor.js?v=12"></script>
	<script src="<%=rootPath%>js/EditorUi.js?v=12"></script>
	<script src="<%=rootPath%>js/Graph.js?v=12"></script>
	<script src="<%=rootPath%>jscolor/jscolor.js"></script>
<script>
Ls.debug = true;
var processId = '${processId}';
var recordId = '${recordId}';
var activityId = '${activityId}';

jQuery(document).ready(function(){
	page_init();
});

function page_init(){
	if (!mxClient.isBrowserSupported()) {
		mxUtils.error('浏览器不支持!', 200, false);  
	}else{
		var container = document.body;  
		// 去锯齿效果  
        mxRectangleShape.prototype.crisp = true; 
		var model = new mxGraphModel(); 
		// 在容器中创建图形   
        var graph = new Graph(container, model);  
		
		// 定义显示样式
		graph.container.setAttribute('tabindex', '0');
		graph.container.style.cursor = 'default';
		graph.container.style.backgroundImage = 'url(' + IMAGE_PATH + '/grid.gif)';
		graph.container.focus();
		
		graph.setConnectable(false);
		// 允许连接
		graph.setConnectable(false);
		// 允许拖动
		graph.setDropEnabled(false);
		// 允许关联
		graph.setPanning(false);
		graph.isCellLocked =function(){return true;};
		
		// 加载xml文件
		//var xml = mxUtils.load('mxgraph.xml');
		// 定义获取当前工作流流程图的xml内容

		Ls.ajax.get(DIAGRAM_PATH,{processId:processId,recordId:recordId,activityId:activityId},function(json){
			// 开始更新事务 
			graph.getModel().beginUpdate(); 
			try{
				Ls.log.add(json.data);
				var xml = mxUtils.parseXml(json.data);

				var root = xml.documentElement;
                var dec = new mxCodec(root.ownerDocument);
				//var dec = new mxCodec(root);
				dec.decode(root, graph.getModel());
			}catch(e){
				mxUtils.alert(mxResources.get('invalidOrMissingFile') + ': ' + e.message);	
			}finally {
				// 结束事务更新
				graph.getModel().endUpdate();    
	
			}
		});
		
	}
}
</script>


</body>
</html>