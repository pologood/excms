// TODO:当前Web应用程序的路径
var WEBCONTEXT = window.__WEB_CONTEXT_PATH ? window.__WEB_CONTEXT_PATH : "";
if(!Ls.isNul(WEBCONTEXT) && WEBCONTEXT.trim()=="/"){
	WEBCONTEXT="";
}


var LS_AJAX_ISREST = false;  // 定义全局的Ajax访问形式,采用rest路径方式
/* 定义弹窗登录页面地址 */
Ls.miniLoginUrl= '/login/miniLogin';
/* 定义模块ID参数 */
var GUR_PARENTID = Ls.url.get("indicatorId");
// 虚拟桌面
//var APP_DESKTOP_HOST = "http://xnzm.hf.cn:6600"; // 公司内部开发平台的桌面地址
var APP_DESKTOP_HOST = "http://hfxnzm.hf.cn:8087"; // 公司内部模拟环境的桌面地址
//var APP_DESKTOP_HOST = "http://10.12.4.96;8080"; // 生产环境的桌面地址
//var APP_DESKTOP_HOST = "http://pxoa.hefei.gov.net"; // 培训环境的桌面地址
//var APP_DESKTOP_HOST = "http://10.12.4.73:8080"; // 合肥试运行平台的桌面地址

var FASTDFS_HOST = window.__FASTDFS_HOST ? window.__FASTDFS_HOST : "";
if(!Ls.isNul(FASTDFS_HOST) && !Ls.string.endWith(FASTDFS_HOST,"/")){
	FASTDFS_HOST += "/";
}
//var FASTDFS_HOST = "http://10.12.4.92/"; //培训环境的桌面地址
//var FASTDFS_HOST= APP_DESKTOP_HOST== "http://xnzm.hf.cn:6600" ? "http://192.168.1.36/" : "http://10.12.4.56/";

// TODO:分布式部署配置
var DEPLOY_CONFIG={
  enable:false,  //本地开发使用false 远程分布式部署请设置为true
  // 系统管理配置
  systemMgr:{
	  enable:false // 是否是分布式部署
	 // ,host:'http://xtgl.hf.cn:6210' // 公司内部开发平台的系统管理应用地址
	  //,host: APP_DESKTOP_HOST= "he" // 合肥试运行平台的系统管理应用地址
	  // ,host: "http://pxsysmgmt.hefei.gov.net:8000" // 公司内部模拟环境的系统管理应用地址
	  //  ,host: "http://hfxtgl.hf.cn:8080" // 公司内部模拟环境的系统管理应用地址
	    ,host: "http://10.12.4.97:8080" // 生产环境
  }
};

Ls.__loadJs('/assets/sms/js/common.js');
