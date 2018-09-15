<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="/app/common/inc/fix-browser.html"%>
<!--[if !(IE)  ]><!--><html lang="zh-CN"><!--<![endif]-->
<head>
	<meta charset="UTF-8" />
	<%@include file="/app/common/inc/comm-head.html"%>
    
<style type="text/css">
/* 如果要隐藏滚动条,请去除下行的注释 */
/*body, html { overflow: hidden; }*/
</style>
</head>
<body>

<!-- 顶部位置导航 -->
<div id="topNav" class="page-header35">
    <span onclick="Ls.page.reload()" class="aicon fonticon" title="刷新本页">&#xe8ba;</span>
    <span class="gray-3">│</span>
    <span class="gray"><i class="fonticon">&#xe8b7;</i> 当前位置：</span>
</div> <!-- topNav -->

<div id="mainBody">
	<div class="main-con">
   		<p>亲，让您白跑一趟，此模块尚在开发中...</p>
	</div> <!-- main-con -->
</div> <!-- mainBody -->        

	
<%@include file="/app/common/inc/comm-footer.html"%>
<script>
// TODO:配置当前页面所有ajax默认的访问地址
//Ls.ajax.url = '';

// TODO:当前页面的全局操作对象
// 通常将当前中一些可变的配置集中管理
// 可根据自己需求处由针对cur对象进行删减和增加
// 好处: 1. 将所有变量放到一个对象中,防止由于个人习惯的变量命名污染JS全局变量
//       2. 在dw中可以自动提示cur对象下的所有方法与属性
//       3. 可变因素集中管理,而不是分散在页面中 
var cur={
	// 为KO准备的viewModel对象
	viewModel : {},
	// 所有当前页面级方法的命名空间对象
	fn:{},
	// 所有当前页面与服务端交互的action命名空间
	action:{
		getMenu : Ls.ajax.url + 'getMenu' // 服务接口名称
	},
	// 所有当前获取jQuery的DOM对象的存储的命名空间对象
	el:{},
	// 获取当前页URL参数的命名空间对象
	params:{
		id : Ls.url.get('id') // 获取Url中的参数	
	},
	// 当前页面要引用到的一些页面地址集中管理对象
	page:{
	}
};
// TODO:入口
jQuery(document).ready(function(){
	
});
	
</script>

</body>
</html>