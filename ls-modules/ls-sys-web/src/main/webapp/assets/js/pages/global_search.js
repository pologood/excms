var global_search = function() {

	var cur = {
		ctrl : "searchCtrl",
		columnId:""
	}

	cur.typeCodes = "articleNews,pictureNews,videoNews,messageBoard,workGuide,interviewInfo,guestBook";
	// cur.typeCodes = "workGuide";
	var init = function(searchWords) {

		mini.parse();

		// grid 对象
		cur.grid = mini.get('datagrid');

		// 设置 grid 加载地址
		cur.grid.setUrl("/content/getQueryPage");
		//debugger
		// 获取弹窗对象，没有弹窗取当前window对象
		cur.win = Ls.getWin.win;
		cur.searchKey = $('#content_search_words');

		if (Ls.getWin.win == window) {
			// 重置 datagrid 高度
			Ls.mini_datagrid_height(cur.grid);
			//var title = searchWords;
			var title = $('#global_search_words').val();
            cur.searchKey.val(title);
		} else {
			// 重置 datagrid 高度
			Ls.mini_datagrid_height(cur.grid, 55);
			// 加载数据
			var title = cur.win.$("#global_search_words").val();
		}

        $('#global_search_words').val('');
		// 初始grid 数据
		cur.grid.load({
			title : title,
			typeCodes : cur.typeCodes,
			sortField:'b.createDate',
			sortOrder:'desc'
		}, function() {
			cur.searchKey.val(title);
		});

		cur.grid.on("beforeselect", function (e) {
			var disabledStr = Ls.publishStatus(e.record.isPublish);
			if (disabledStr) {
				e.cancel = true
			}
		});
	}

	function search() {
		var keyValue = cur.searchKey.val();
		// 初始grid 数据
		cur.grid.load({
			title : keyValue,
			typeCodes : cur.typeCodes,
			columnId:cur.columnId,
			pageIndex:cur.grid.pageIndex
		});
	}

	function searchByStatus(ispub) {
		var keyValue = cur.searchKey.val();
		cur.pubStatus = ispub;
		cur.grid.load({
			title : keyValue,
			typeCodes : cur.typeCodes,
			isPublish : cur.pubStatus,
			columnId:cur.columnId,
			pageIndex:cur.grid.pageIndex
		});
	}

	function showAll() {
		cur.searchKey.val('');
		cur.columnId = '';
		$('#column_select').val('');
		search();
	}

	function titleLink(e) {
		var link = "", style = "";
		var rec = e.record;

		if (Ls.isEmpty(rec.redirectLink)) {
			if ("public_content" == rec.typeCode) {
				link = Ls.getWin.win.GLOBAL_HTMLFILEPATH + "/public/content/" + rec.id;
			} else {
				link = Ls.getWin.win.GLOBAL_HTMLFILEPATH + "/content/article/" + rec.id;
			}
		} else {
			link = rec.redirectLink;
		}

		if (rec.titleColor) {
			style = 'color:' + rec.titleColor + '';
		}

		if (rec.isBold == 1) {
			style += "font-weight:bold;";
		}

		if (el.isTilt == 1) {
			style += "font-style:italic;";
		}

		if (el.isUnderline == 1) {
			style += "text-decoration:underline;";
		}
		if ("articleNews" == rec.typeCode) {
			link = '<a href="javascript:void(0)" style="' + style + '" onclick="editArticleNews('+ rec.id + ',' + rec.columnId+')"><ins>' + rec.title + '</ins></a>';
		}else{
			link = '<a href="' + link + '" style="' + style + '" target="_blank"><ins>' + rec.title + '</ins></a>'
		}


		return link;
	}

	function operation(e) {
		var rec = e.record, btnStr = ''
		var disabledStr = Ls.publishStatus(rec.isPublish);
		var publishStr='';
		/*if(rec.isPublish=='1'){
		 publishStr='<li><a href="javascript:void(0)" onclick="publish('+ rec.id +')">重新发布</a></li>' ;
		 }*/
		btnStr += '<button ' + disabledStr + ' class="btn btn-default btn-sm btn-edit" onclick="global_search.edit(' + rec.id + ',' + rec.columnId + ',\'' + rec.typeCode + '\')">修 改</button> ';
		btnStr += '<button ' + disabledStr + ' class="btn btn-default btn-sm btn-delete" onclick="global_search.del(' + rec.id + ',\''+ rec.typeCode +'\')">删 除</button> ';
		return btnStr;
	}

    function publish(e) {
        var rec = e.record, str = '';
        str = rec.isPublish == 1 ? '已发布' : '<span style="color: red;">未发布</span>';
        return str;
    }

	// 修改信息
	function edit(id, columnId,typeCode) {
		var _H = $(window).height() - 100,
			_W = $(window).width() - 100;
		art.dialog.data('siteId', GLOBAL_SITEID);
		Ls.openWin('/todolist/showDetail?typeCode=' + typeCode + '&columnId=' + columnId + '&id=' + id, {
			id : 'record',
			title : '内容详情',
			width : _W + 'px',
			height : _H + 'px',
			padding : '0',
			lock : true,
		});
	}

	// 删除信息
	function del(id,typeCode) {
		if (confirm("确定删除选中记录？")) {
			var ids = [];
			var url ="";
			if(id != null && id != "") {
				ids.push(id);
			} else {
				var records = cur.grid.getSelecteds();
				if ((null == records || records.length <= 0) && ids.length <= 0) {
					Ls.tips("请选择一条记录!", {times: 2});
					return;
				}
				for(var i = 0; i < records.length; i++) {
					ids.push(records[i].id);
				}
			}
			if(typeCode == 'articleNews' || typeCode == 'pictureNews') {
				url = "/articleNews/delete";
			}else {
				url = "/globalSearch/batchDelete";
			}
			Ls.ajaxGet({
				data : {
					ids : ids
				},
				url : url,
				success : function(resp) {
					if (resp.status == 1) {
						Ls.tips("删除完成，正在生成处理中", {times: 2});
						search();
					} else {
						Ls.tips(resp.desc, {icon:'error',times: 2});
					}
				}
			});
		}
	}

	//发布
	function batchPublish(id,status) {
		if (confirm("确定" + status == 0?"取消发布":"发布" + "选中记录？")) {
			var ids = [];
			if(id != null && id != "") {
				ids.push(id);
			} else {
				var records = cur.grid.getSelecteds();
				if ((null == records || records.length <= 0) && ids.length <= 0) {
					Ls.tips("请选择一条记录!", {times: 2});
					return;
				}
				for(var i = 0; i < records.length; i++) {
					ids.push(records[i].id);
				}
			}
			Ls.ajaxGet({
				data : {
					ids : ids,
					status:status
				},
				url : "/globalSearch/batchPublish",
				success : function(resp) {
					if (resp.status == 1) {
						Ls.tips(status == 0?"取消发布":"发布" + "成功", {times: 2});
						search();
					} else {
						Ls.tips(resp.desc, {icon:'error',times: 2});
					}
				}
			});
		}
	}

    function getStatusMsg() {
        var keyValue = cur.searchKey.val();
        cur.grid.load({title: keyValue, typeCodes : typeCodes});
    }
     //消息回调
	indexMgr.publish = function (d) {
		cur.grid.findRows(function (row) {
			if ($.inArray(row.id, d.contentIds) >= 0) return true;
		}).filter(function (v, i) {
			v.isPublish = d.isPublish;
			cur.grid.updateRow(v);
		});
	}
	return {
		cur:cur,
		init : init,
		search : search,
		showAll : showAll,
		titleLink : titleLink,
		operation : operation,
        publish : publish,
        searchByStatus : searchByStatus,
		edit : edit,
		del : del,
		batchPublish:batchPublish
	}

}();