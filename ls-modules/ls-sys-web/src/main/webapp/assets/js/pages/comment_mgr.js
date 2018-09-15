var content_mgr = function () {

  var elem_tree = "content_tree";
  var ztree_settings = {
    view: {
      nameIsHTML: true,
      selectedMulti: false,
      dblClickExpand: false,
      addDiyDom: addDiyDom
      //showIcon: false,
      //showLine: false
    },
    async: {
      enable: true,
      type: "GET",
      dataType: "JSON",
      url: "",
      otherParam: {},
      autoParam: ""
    },
    callback: {}
  };

  function initSlimScroll() {
    //添加模拟滚动条
    var ui_tree = $('#content_tree');
    var ui_layout = $(".mini-layout-region-body");
    ui_tree.attr("data-height", ui_layout.height() - 10);
    App.initSlimScroll(ui_tree);
  }

  function getFont(treeId, node) {
    return node.font ? node.font : {};
  }
  var isRoot=true;
  //站点树数据过滤器
  function dataFilter(treeId, parentNode, responseData) {
	
    var root = responseData.data;
    var responseData = Ls.treeDataFilter(root, Ls.treeDataType.SITE);
    if(isRoot){ 
    var root = {
            "id": 0,
            "name": "栏目列表",
            children: responseData,
            open:true
        }
    isRoot=false;
	}
    return root;
  };

  //加载站点树
  function GetSiteColumnTree() {
    var settings = $.extend(true, ztree_settings, {
      view: {
        fontCss: getFont
      },
      async: {
        url: "/siteMain/getColumnByIsComment",
        otherParam: {"indicatorId": GLOBAL_SITEID},
        autoParam: ["indicatorId=indicatorId"],
        dataFilter: dataFilter
      },
      callback: {
        onClick: onClick,
        beforeClick: function (treeId, treeNode, clickFlag) {
          if (treeNode.isStartUrl == 1) {
            return false;
          }
        },
        onAsyncSuccess: function () {
          content_mgr.ztree = $.fn.zTree.getZTreeObj(elem_tree);
          //添加模拟滚动条
          App.initContentScroll();
        }
      }
    });

    function onClick(event, treeId, node) {
      if(node.id==0){
    	  showAllComment();
      }
      if (node.isParent) {
        content_mgr.ztree.cancelSelectedNode(node);
        content_mgr.ztree.expandNode(node);
        event.stopPropagation();
      } else {

        //写入当前节点对象，用于其它模块获取方便
        content_mgr.node = node;

        //此方法以后不再使用
        content_mgr.indicatorId = node.indicatorId;
        if (content_mgr.indicatorId == 48810) {

          getContentApp("/admin/survey_list.html?id=1");

        } else {
          //加载内容
          getContentApp("/commentMgr/listPage?indicatorId=" + node.indicatorId);
        }

        return false;
      }
    }

    //初始化树
    $.fn.zTree.init($("#" + elem_tree), settings);
  }

  //加载内容
  var getContentApp = function (url, options) {
    return App.getContentAjax(url, options).done(function (res) {
      $("#content_body").html(res)
    });
  }

  function addDiyDom(treeId, node){
  	if(!node.isParent){
  		var aObj = $("#" + node.tId + "_a");
  		aObj.append("<span style='color:red'> ["+ node.commentNum+"]</span>");
  	}
  	
  }
  

  var init = function () {
    //初始化布局
    mini.parse();
    GetSiteColumnTree();
    getContentApp("/commentMgr/listPage?siteId=" + GLOBAL_SITEID);
    /*var content_body = $("#content_body");
     content_body.width(content_body.parent().width()).height(content_body.parent().height());*/
  }

  return {
    init: init,
    getContentApp: getContentApp
  }

}();