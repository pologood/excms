var organ_catalog_select_tree = function() {

	var init = function(id, selectIds) {
		var arr = [];// 选中
		if (selectIds && selectIds != "") {
			arr = selectIds.split(",");
		}
		// 设置
		var ztree_settings = {
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
			async : {
				enable : true,
				url : "/public/catalog/getOrganCatalogTree",
				autoParam : [ "id=parentId", "organId" ],
				otherParam : [ "catalog", "true", "all", "false" ],
				dataFilter : function(treeId, parentNode, responseData) {
					for (var i = 0, l = responseData.length; i < l; i++) {
						var node = responseData[i];
						node.nocheck = true;
						if (!node.isParent) {// 叶子节点
							node.isHidden = true;
							if (cur.node) {
								if (node.type == cur.node.type && node.id != cur.node.id) {// 不能选择自己
									node.nocheck = false;
									node.isHidden = false;
									if (arr.indexOf(node.organId + "_" + node.id) > -1) {// 设置选中，部门id加目录id
										node.checked = true;
									}
								}
							} else {// 给其他内容管理同步使用
								if (node.type == "DRIVING_PUBLIC") {// 同步到主动公开
									node.nocheck = false;
									node.isHidden = false;
									if (arr.indexOf(node.organId + "_" + node.id) > -1) {// 设置选中，部门id加目录id
										node.checked = true;
									}
								}
							}
						}
					}
					return responseData;
				}
			},
			data : {
				simpleData : {
					enable : true,
					idKey : 'id',
					pIdKey : 'parentId'
				}
			},
			callback : {
				onClick : function(event, treeId, node) {
					var tree = $.fn.zTree.getZTreeObj(treeId);
					if (node.nocheck) {
						tree.expandNode(node, !node.open, false, true, true);
						tree.cancelSelectedNode(node);
					} else {
						tree.checkNode(node, !node.checked);
						tree.setting.callback.onCheck(null, treeId, node);
					}
					return false;
				},
				onCheck : function(e, treeId, node) {
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
							ids += checks[i].organId + "_" + checks[i].id;
							names += checks[i].name;
						}
						$("#" + id + "Ids").val(ids);
						$("#" + id + "Names").val(names);
					}
				}
			}
		};
		$.fn.zTree.init($("#" + id + "_tree"), ztree_settings);
	}
	return {
		init : init
	}
}();