// 工作动态管理
var publicWorksManage = function() {

	var init = function() {
		// 初始化布局
		mini.parse();
		// 获取datagrid
		cur.grid = mini.get("works_datagrid");
		cur.grid.load({
			siteId : GLOBAL_SITEID
		});
		// 重置 datagrid 高度
		Ls.mini_datagrid_height(cur.grid);
	}

	var search = function(form) {
		var json = $(form).serializeObject();
		json.siteId = GLOBAL_SITEID;
		cur.grid.load(json);
	}

	var searchClear = function(form) {
		$(form)[0].reset();
		search(form);
	}

	return {
		init : init,
		search : search,
		searchClear : searchClear
	}
}();

function renderOp(e) {
	var str = "";
	var rec = e.record;
	if (rec.enable) {
		str += '<button class="btn btn-default btn-sm btn-edit" onclick="updateStatus(' + rec.id + ')">禁用</button> '
	} else {
		str += '<button class="btn btn-default btn-sm btn-edit" onclick="updateStatus(' + rec.id + ')">启用</button> '
	}
	str += '<button class="btn btn-default btn-sm btn-edit" onclick="edit(' + rec.id + ')">修改</button> '
	str += '<button class="btn btn-default btn-sm btn-delete" onclick="deleteData(' + rec.id + ')">删除</button>'
	return str;
}

function renderEnable(e) {
	var str = "";
	var rec = e.record;
	if (rec.enable) {
		str += '<img src="/assets/images/tick.png" />';
	} else {
		str += '<img src="/assets/images/del.png" />';
	}
	return str;
}

function updateStatus(id) {
	// 提交
	Ls.ajaxGet({
		url : "/public/works/updateStatus?id=" + id
	}).done(function(d) {
		if (d.status == 1) {
			Ls.tipsInfo('操作成功!');
			cur.grid.reload();
		} else {
			Ls.tipsErr(d.desc);
		}
	});
}

function edit(id) {
	cur.id = id;
	Ls.openWin(cur.edit_url, '460px', '400px', {
		id : 'works_button_saveOrUpdate',
		title : "修改工作动态",
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

function add() {
	cur.id = "";
	Ls.openWin(cur.edit_url, '460px', '400px', {
		id : 'works_button_saveOrUpdate',
		title : "新增工作动态",
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

function deleteData(id) {
	Ls.ajax({
		url : "/public/works/delete?id=" + id
	}).done(function(text) {
		if (text.status == 1) {
			Ls.tipsOk("删除成功");
			cur.grid.reload();
		} else {
			Ls.tipsErr(text.desc);
		}
	});
}