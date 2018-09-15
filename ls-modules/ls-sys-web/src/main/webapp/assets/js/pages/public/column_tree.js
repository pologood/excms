var column_select_tree = function() {
	var init = function(id, selectIds) {
		var setting = {
			view : {
				nameIsHTML : true,
				showTitle : true,
				selectedMulti : false,
				dblClickExpand : false,
				expandSpeed : "fast"
			},
			check : {
				enable : true,
				nocheckInherit : false
			},
			callback : {
				onCheck : onCheck,
				onClick : onClick
			},
			data : {
				keep : {},
				key : {
					title : "name"
				},
				simpleData : {
					enable : true,
					idKey : 'indicatorId',
					pIdKey : 'parentId'
				}
			}
		};

		function onClick(e, treeId, node) {
			var tree = $.fn.zTree.getZTreeObj(treeId);
			if (node.nocheck) {
				tree.expandNode(node);
				tree.cancelSelectedNode(node);
			} else {
				tree.checkNode(node, !node.checked);
				tree.setting.callback.onCheck(null, treeId, node);
			}
		}

		function onCheck(e, treeId, node) {
			var tree = $.fn.zTree.getZTreeObj(treeId);
			var checks = tree.getCheckedNodes(true);
			// 为空
			$("#" + id + "Ids").val("");
			$("#" + id + "Names").val("");
			if (checks.length > 0) {
				var ids = "", names = "";
				for (var i = 0, l = checks.length; i < l; i++) {
					if (i > 0) {
						ids += ",";
						names += ",";
					}
					ids += checks[i].indicatorId;
					names += checks[i].name;
				}
				$("#" + id + "Ids").val(ids);
				$("#" + id + "Names").val(names);
			}
			return false;
		}

		Ls.ajax({
			url : "/siteMain/getAllColumn",
			data : {
				siteId : GLOBAL_SITEID,
				contentModelCode : cur.contentModel.contentModelCode
			},
		}).done(function(json) {
			if (json.data == 1) {
				Ls.tipsErr("参数错误，站点为空");
				return;
			}
			var data = json.data;
			if (data != null) {
				var arr = [];

				if (selectIds && selectIds != "") {
					arr = selectIds.split(",");
				}
				for (var i = 0, l = data.length; i < l; i++) {
					var node = data[i];
					node.nocheck = true;
					if (!node.isParent) {// 叶子节点
						node.isHidden = true;
						if (data[i].columnTypeCode == "articleNews") {
							node.nocheck = false;
							node.isHidden = false;
							if (arr.indexOf("" + data[i].indicatorId) > -1) {// 设置选中
								node.checked = true;
							}
						}
					}
				}
				Ls.treeDataFilter(data, Ls.treeDataType.SITE);
			}
			$.fn.zTree.init($("#" + id + "_tree"), setting, data);
		});
	}
	return {
		init : init
	}
}();