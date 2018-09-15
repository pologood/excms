<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title></title>
<link rel="stylesheet" href="/assets/res/css/common.css">
<link rel="stylesheet" href="/assets/res/css/icons.css">
 
</head>
<body>
<div class="page-header">
<a href="javascript:Ls.page.reload();" class="a-icon1" title="ˢ�±�ҳ"><i class="icon-ccw"></i></a>
<span class="gray-3"></span>
<span class="f13 gray"><i class="icon-right-dir"></i></span>
</div> 

<div class="wrap">
	<div id="time"></div>
	<div id="msg"></div>
<form id="form1">
<table class="tform">
<tbody>
<tr>
<td class="tdlabel">设置flowId：</td>
<td colspan="3">
	<input type="text" class="text" name="activityId" id="activityId" >  <br />
	<input type="text" class="text" name="name" id="name" >
</td>
</tr>
</tbody>
<tfoot class="pageShow">
<tr>
	<td colspan="4" class="tc pd5">
	<hr>
	<button class="btn btn-primary btn-small" type="button" onClick="save()"> 保 存 </button>
	</td>
</tr>
</tfoot>
</table>
</form>
    


</div>

<script src="/assets/core/jquery.min.js"></script>
<script src="/assets/core/boot.js" debug="1" font-icon="1"></script>

<script>
//Ls.ajax.url=rpc.admin.mgr.sys;
var cur={
	viewModel : {},
	fn:{},
	action:{
		
	},
	el:{},
	params:{
		flowId : Ls.url.get("activityId") || ""	
	},
	pWin : parent.window
	
	
	
};

jQuery(document).ready(function(){
	cur.el.activityId = $("#activityId");
	var t = new Date().getTime();
	$("#time").text(t);
	if(parent.window.SELECT_MODEL){
		var model = parent.window.SELECT_MODEL;
		console.log(model);
		console.log(model.getValue());
		
		$("#name").val(model.getValue());	
	}
	
	if(cur.params.activityId==""){
		$("#msg").text("这是一个新模型,请关键相关的工作流图元类型!");
	}else{
		$("#msg").text("这是一个已关联的模型,对应的activityId=" + cur.params.activityId);
		
		cur.el.activityId.val(cur.params.activityId);
	}
});

function save(){
	var fid = cur.el.activityId.val();
	var name = $("#name").val();
	if(fid!=""){
		Ls.log.add("in attr.html >>>")
		//Ls.log.add(parent.window.SELECT_MODEL);
		//var cell = parent.window.SELECT_MODEL;
		Ls.log.add(parent.window.editorUi);
		parent.window.editorUi.editor.updateCell(fid,name);
		
		//cell.activityId=fid;	
		//cell.setValue(name);
		//cur.el.activityId.val('');	
		//Ls.log.add(parent.window.SELECT_MODEL);
	}
}



</script>


</body>
</html>