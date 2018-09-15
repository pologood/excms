// 分页
var cur = {
	guestbook_datagrid : null,
	key : ""
};
var guestbook_center_manage = function() {

	// 拉取数据
	function getData(key) {
		cur.key = key;
		cur.guestbook_datagrid.load({
			title : key,
			siteId : GLOBAL_SITEID
		});
	}

	var init = function() {
		// 初始化布局
		mini.parse();
		// 获取datagrid
		cur.guestbook_datagrid = mini.get("guestbook_datagrid");
		guestbook_center_manage.getData('');
		Ls.mini_datagrid_height(cur.guestbook_datagrid);
	};

	return {
		init : init,
		getData : getData
	};
}();
// 操作
function renderTitle(e) {
	var rec = e.record;
	var str = rec.title;
	if (rec.isTimeOut == 2) {
		str += '<a href="javascript:void(0)" style="color:yellow;" title="黄牌警告">(黄)</a>';
	} else if (rec.isTimeOut == 3) {
		str += '<a href="javascript:void(0)" style="color:red;" title="红牌警告">(红)</a>';
	} else if (rec.isTimeOut == 1) {
		str += '<a href="javascript:void(0)" style="color:gray;" title="超时警告">(超)</a>';
	}
	return str;
}
function renderOp(e) {
	var str = "";
	var rec = e.record;
	str += '<button class="btn btn-default btn-sm btn-edit" onclick="forward(\'' + rec.id + '\',\'' + rec.columnId + '\')">转办</button> ';
	str += '<button class="btn btn-default btn-sm btn-edit" onclick="forwardRecord(\'' + rec.id + '\')">转办记录</button> ';
	str += '<button class="btn btn-default btn-sm btn-edit" onclick="supervise(\'' + rec.id + '\')">督办</button> ';
	str += '<button class="btn btn-default btn-sm btn-edit" onclick="superviseRecord(\'' + rec.id + '\')">督办记录</button> ';
	str += '<button class="btn btn-default btn-sm btn-delete" onclick="doDelete(\'' + rec.id + '\')">删除</button>';
	return str;
}
function searchContent() {
	var key = $("#searchKey").val();
	guestbook_center_manage.getData(key);
}
function searchClear() {
	$("#searchKey").val('');
	guestbook_center_manage.getData('');
}
function forward(id, columnId) {
	Ls.openWin('/guestBook/guestBookForward?id=' + id + '&columnId=' + columnId, '460px', '250px', {
		id : 'guestBook_button_forward',
		title : "留言转办",
		ok : function() {
			var iframe = this.iframe.contentWindow;
			if (!iframe.document.body) {
				return false;
			}
			iframe.$("#" + iframe.cur.id).submit();
			return false;
		}
	});
}
function forwardRecord(id) {
	Ls.openWin('/guestBook/queryForwardRecord?id=' + id, '700px', '400px', {
		id : 'guestBook_button_forward_record',
		title : '转办记录'
	});
}
function supervise(id) {

}
function superviseRecord(id) {

}
function doDelete(id) {
	var ids = [];
	if ((id || '') != '') {
		ids.push(id);
	} else {
		var rows = cur.guestbook_datagrid.getSelecteds();
		if (rows.length == 0) {
			Ls.tipsInfo("至少选择一项");
			return;
		}
		for (var i = 0, l = rows.length; i < l; i++) {
			ids[i] = rows[i].id;
		}
	}
	if (confirm("确定删除选中记录？")) {
		Ls.ajaxGet({
			data : {
				ids : ids
			},
			url : "/guestBook/batchDelete",
			success : function(text) {
				Ls.tipsOk("删除成功！");
				guestbook_center_manage.getData(cur.key);
			}
		});
	}
}