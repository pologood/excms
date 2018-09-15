// 任务管理
var jobManage = function() {

	var init = function() {
		// 初始化布局
		mini.parse();
		// 获取datagrid
		cur.grid = mini.get("job_datagrid");
		cur.grid.load({
			siteId : GLOBAL_SITEID
		});
		// 重置 datagrid 高度
		Ls.mini_datagrid_height(cur.grid);
	}
	return {
		init : init
	}
}();

function renderOp(e) {
	var str = "", title = "";
	var rec = e.record;
	if ("NORMAL" == rec.status) {
		title = "暂停";
	} else if ("PAUSED" == rec.status) {
		title = "恢复";
	}
	str += '<button class="btn btn-default btn-sm btn-stop" onclick="pauseOrResumeJob(' + e.rowIndex + ',' + rec.id + ')">' + title + '</button> '
	str += '<button class="btn btn-default btn-sm btn-edit" onclick="edit(' + e.rowIndex + ',' + rec.id + ')">修改</button> '
	str += '<button class="btn btn-default btn-sm btn-run" onclick="triggerJob(' + rec.id + ')">立即运行一次</button> '
	str += '<button class="btn btn-default btn-sm btn-delete" onclick="deleteData(' + rec.id + ')">删除</button>'
	return str;
}

function pauseOrResumeJob(rowIndex, id) {
	var row = cur.grid.getRow(rowIndex);
	var url = "", title = "";
	if ("NORMAL" == row.status) {
		title = "暂停";
		url = "/job/pauseJob?id=" + id;
	} else if ("PAUSED" == row.status) {
		title = "恢复";
		url = "/job/resumeJob?id=" + id
	}
	Ls.ajax({
		url : url
	}).done(function(text) {
		if (text.status == 1) {
			Ls.tipsOk(title + "成功");
			cur.grid.updateRow(row, text.data);
		} else {
			Ls.tipsErr(text.desc);
		}
	});
}

function triggerJob(id) {
	Ls.ajax({
		url : "/job/triggerJob?id=" + id
	}).done(function(text) {
		if (text.status == 1) {
			Ls.tipsOk("运行成功");
			cur.grid.reload();
		} else {
			Ls.tipsErr(text.desc);
		}
	});
}

function edit(rowIndex, id) {
	cur.rowIndex = rowIndex;
	cur.id = id;
	Ls.openWin('/job/edit', '460px', '350px', {
		id : 'job_button_saveOrUpdate',
		title : "修改任务",
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
	Ls.openWin('/job/edit', '460px', '350px', {
		id : 'job_button_saveOrUpdate',
		title : "新增任务",
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
		url : "/job/delete?id=" + id
	}).done(function(text) {
		if (text.status == 1) {
			Ls.tipsOk("删除成功");
			cur.grid.reload();
		} else {
			Ls.tipsErr(text.desc);
		}
	});
}