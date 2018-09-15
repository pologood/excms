// 菜单管理
var menuManage = function() {

	var isInit = false;

	var ztree_settings = {
//		view : {
//			showTitle : false
//		},
		async : {
			enable : true,
			url : ""
		}
	};

	var saveOrUpdate = function(id, type) {
		if (!id) {
			cur.update_type = 1;
		} else {
			cur.update_type = 2;
		}
		cur.id = id;
		cur.entity_type = type;
		var title = (!id ? "新增" : "编辑") + (type == 1 ? "菜单" : "按钮");
		Ls.openWin('/system/menu/edit?type=' + type, '460px', '400px', {
			id : 'menu_button_saveOrUpdate',
			title : title,
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

	var menu = function() {
		// 初始化布局
		mini.parse();
		// 获取datagrid
		cur.grid = mini.get("menu_datagrid");
		// 重置 datagrid 高度
		Ls.mini_datagrid_height(cur.grid);

		var settings = $.extend(true, ztree_settings, {
			view : {
				addDiyDom : addDiyDom
			},
			async : {
				// 查询所有菜单
				url : cur.menu_url,
				dataFilter : dataFilter
			},
			callback : {
				onClick : onClick,
				onAsyncSuccess : function() {
					var nodes = cur.tree.getNodes();
					if (nodes.length > 0) {
						cur.tree.setting.callback.onClick(null, null, nodes[0]);
					}

					// 加载列表
					if (!isInit) {
						isInit = true;
						// 添加模拟滚动条
						// App.initContentScroll();
                        App.initContentScroll(null, '#ui_tree', {right: true});
					}
				}
			}
		});

		cur.tree = $.fn.zTree.init($("#" + cur.tree_id), settings);

		// 点击进行编辑
		function onClick(event, treeId, node) {
			// 当选择的节点为root时，默认点击第一个节点的点击事件
			if (node.id == cur.root_id) {
				var n = node.children[0];
				cur.tree.setting.callback.onClick(null, treeId, n);
				return;
			}
			cur.node = node;
			cur.tree.selectNode(node);
			if (cur.update_type != 3) { // 删除时不操作
				cur.tree.expandNode(node);
			}
			// 加载按钮列表
			loadGrid(node);
		}

		function dataFilter(treeId, parentNode, responseData) {
			// 构造根节点
			var root = {
				"id" : cur.root_id,
				"name" : "菜单列表",
				children : Ls.treeDataFilter(responseData, Ls.treeDataType.MENU)
			}
			return root;
		}

		function addDiyDom(treeId, node) {
			var aObj = $("#" + node.tId + "_a");
			var addBtn = ' <span class="button add-a" id="addBtn_' + node.id + '" title="' + node.name + '"></span>';
			var editBtn = ' <span class="button edit-a" id="editBtn_' + node.id + '" title="' + node.name + '"></span>';
			var delBtn = ' <span class="button del-a" id="delBtn_' + node.id + '" title="' + node.name + '"></span>';

			// 说明有父节点
			if (node.id != cur.root_id) {
				addBtn = addBtn + editBtn + delBtn;
			}
			aObj.after(addBtn);

			var addBtn = $("#addBtn_" + node.id);
			var editBtn = $("#editBtn_" + node.id);
			var delBtn = $("#delBtn_" + node.id);

			// 如果是父节点，不显示删除按扭
			if (node.isParent) {
				delBtn.hide();
			}

			// 添加
			addBtn && addBtn.on("click", function() {
				cur.node = node;
				saveOrUpdate(null, 1);
				return false;
			});

			// 修改
			editBtn && editBtn.on("click", function() {
				cur.node = node;
				cur.tree.selectNode(node);
				// 获取数据绑定
				saveOrUpdate(node.id, 1);
				return false;
			});

			// 删除
			delBtn && delBtn.on("click", function() {
				cur.update_type = 3;
				deleteNode(node);
				return false;
			});

		}

		var loadGrid = function(node) {
			// datagrid重新渲染
			cur.grid.load({
				indicatorId : node.id
			});
		}

		var deleteNode = function(node) {
			if (confirm('真的要删除吗？')) {
				Ls.ajaxGet({
					url : "/system/indicator/delete",
					data : {
						ids : [ node.id ]
					}
				}).done(function(d) {
					Ls.tipsOk('删除成功!');
					// 删除本节点
					cur.tree.removeNode(node);
					var parentNode = null;
					if (node.pId) {
						parentNode = cur.tree.getNodeByParam("id", node.pId, null);
						if (parentNode.children.length == 0) {
							$("#delBtn_" + parentNode.id).show();
						}
						cur.tree.setting.callback.onClick(null, null, parentNode);
					} else {
						var nodes = cur.tree.getNodes();
						if (nodes.length > 0) {
							cur.tree.setting.callback.onClick(null, null, nodes[0]);
						}
					}
				});
			}
		}
	}
	return {
		saveOrUpdate : saveOrUpdate,
		menu : menu
	}
}();