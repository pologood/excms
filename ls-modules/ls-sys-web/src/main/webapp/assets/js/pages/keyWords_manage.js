// 关键词搜索
var keyWordsManage = function() {

	var init = function(edit) {
		// 初始化布局
		mini.parse();
		// 获取datagrid
		cur.keyWords_grid = mini.get("keyWords_datagrid");
		cur.keyWords_grid.load({
			siteId : GLOBAL_SITEID
		});
		cur.sort_grid = mini.get("sort_datagrid");
		cur.sort_grid.load({
			siteId : GLOBAL_SITEID
		});
		Ls.mini_datagrid_height(cur.keyWords_grid, 70);
		Ls.mini_datagrid_height(cur.sort_grid, edit ? 120 : 70);

		/*
		 * 解决tab切换时组建加载不全问题
		 */
		$('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
			var id = e.currentTarget.hash;
			if (id == '#tab_1_1') {
				cur.keyWords_grid.reload();
			} else if (id == '#tab_1_2') {
				cur.sort_grid.reload();
			}
		});
	}
	return {
		init : init
	}
}();

function renderOp(e) {
	var str = "";
	var rec = e.record;
	str += '<button class="btn btn-default btn-sm btn-edit" onclick="saveToSort(\'' + rec.keyWords + '\')">添加至排序</button> ';
	return str;
}

function renderOpSort(e) {
	var str = "";
	var rec = e.record;
	str += '<button class="btn btn-default btn-sm btn-edit" onclick="edit(\'' + rec.id + '\')">修改</button> ';
	str += '<button class="btn btn-default btn-sm btn-delete" onclick="deleteData(\'' + rec.id + '\')">删除</button> ';
	return str;
}

function saveToSort(keyWords) {
	Ls.ajax({
		url : "/heatAnalysis/keyWords/saveToSort",
		data : {
			siteId : GLOBAL_SITEID,
			keyWords : keyWords
		}
	}).done(function(text) {
		if (text.status == 1) {
			Ls.tips("添加成功", {
				icons : "success"
			});
			cur.sort_grid.reload();
		} else {
			Ls.tips(text.desc, {
				icons : "error"
			});
		}
	});
}

function edit(id) {
	cur.id = id;
	Ls.openWin('/heatAnalysis/keyWords/edit', '460px', '250px', {
		id : 'keyWords_button_saveOrUpdate',
		title : "修改关键词",
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
	Ls.openWin('/heatAnalysis/keyWords/edit', '460px', '250px', {
		id : 'keyWords_button_saveOrUpdate',
		title : "新增关键词",
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
	var ids = [];
	if (id != '') {
		ids.push(id);
	} else {
		var rows = cur.sort_grid.getSelecteds();
		if (rows.length == 0) {
			Ls.tips("至少选择一项", {
				icons : "info"
			});
			return;
		}
		for (var i = 0, l = rows.length; i < l; i++) {
			ids[i] = rows[i].id;
		}
	}
	if (confirm('真的要删除吗？')) {
		Ls.ajaxGet({
			url : "/heatAnalysis/keyWords/delete",
			data : {
				ids : ids
			}
		}).done(function(d) {
			if (d.status == 1) {
				Ls.tips("删除成功", {
					icons : "success"
				});
				cur.sort_grid.reload();
			} else {
				Ls.tips(d.desc, {
					icons : "error"
				});
			}
		});
	}
}