// 信息公开目录选择
var publicCatalogSelect = function () {
    /**设置*/
    var settings = {
        check: {
            enable: true,
            chkboxType: {"Y": "p", "N": "ps"}
        },
        data: {
            key: {
                title: "id"
            },
            simpleData: {
                enable: true,
                idKey: 'id',
                pIdKey: 'parentId'
            }
        },
        callback: {
            onClick: function (event, treeId, node) {
                if (node.nocheck) {
                    cur.public_catalog_tree.cancelSelectedNode(node);
                    cur.public_catalog_tree.expandNode(node, !node.open, false, true, true);
                } else {
                    cur.public_catalog_tree.checkNode(node, !node.checked, true);
                }
            }
        }
    }

    function getCheckNodeByParentId(node) {
        return node.level > 0 && node.checked;
    }

    function init() {
        // 初始化布局
        mini.parse();
        // 选择完毕方法
        cur.ok = function () {
            var data = {};
            var nodes = cur.public_catalog_tree.getNodes();
            if (nodes && nodes.length > 0) {
                var ids = [], names = [];
                for (var i = 0, l = nodes.length; i < l; i++) {
                    if (!nodes[i].checked) {
                        continue;
                    }
                    // 查找该节点选中的节点数据
                    var childrenIds = [], childrenNames = [];
                    var selectNodes = cur.public_catalog_tree.getNodesByFilter(getCheckNodeByParentId, false, nodes[i]);
                    if (selectNodes && selectNodes.length > 0) {
                        for (var j = 0, ll = selectNodes.length; j < ll; j++) {
                            childrenIds.push(selectNodes[j].id);
                            childrenNames.push(selectNodes[j].name);
                        }
                    }
                    ids.push(nodes[i].id + (childrenIds.length > 0 ? ("-" + childrenIds.join(",")) : ""));
                    names.push(nodes[i].name + (childrenIds.length > 0 ? ("\r\n" + childrenNames.join(",")) : ""));
                }
                data.selectIds = ids.join("|");
                data.selectNames = names.join("\r\n");
            }
            return data;
        }
        //加载目录
        Ls.ajax({
            url: cur.public_catalog_url,
        }).done(function (data) {
            //处理data，只能叶子节点选择，并勾选已经选中的节点
            var selectIds = cur.W.cur.vm.keyIds;
            for (var i = 0, l = data.length; i < l; i++) {
                if (data[i].isParent && data[i].parentId != 0) {
                    data[i].nocheck = true;
                } else if (selectIds && selectIds.indexOf(data[i].id) > -1) {
                    data[i].checked = true;
                }
            }
            // 添加模拟滚动条
            // App.initContentScroll(null, "#" + cur.catalog_tree_id);
            // 树
            cur.public_catalog_tree = $.fn.zTree.init($("#" + cur.catalog_tree_id), settings, data);
        });
    }

    return {
        init: init
    }
}();