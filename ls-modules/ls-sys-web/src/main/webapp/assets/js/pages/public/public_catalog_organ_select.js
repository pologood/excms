var public_catalog_organ_select = function () {

    // 公共属性
    var setting = {
        view: {
            nameIsHTML: true,
            selectedMulti: false,
            dblClickExpand: false,
            expandSpeed: "fast"
        },
        check: {
            enable: true
        },
        data: {
            simpleData: {
                enable: true,// 简单模式
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
    };

    function getCheckNodeByParentId(node) {
        return node.level > 0 && node.checked;
    }

    // 信息公开树，不跨站点，传入当前操作的节点公开类型和目录id
    var init = function (treeId, type, id, selectIds) {
        cur.ok = function () {// 选择成功回调函数
            var data = {};
            var nodes = cur.public_catalog_tree.getNodes();
            if (nodes && nodes.length > 0) {
                var ids = [], names = [];
                for (var i = 0, l = nodes.length; i < l; i++) {
                    // 查找该节点选中的节点数据
                    var childrenIds = [], childrenNames = [];
                    var selectNodes = cur.public_catalog_tree.getNodesByFilter(getCheckNodeByParentId, false, nodes[i]);
                    if (selectNodes && selectNodes.length > 0) {
                        for (var j = 0, ll = selectNodes.length; j < ll; j++) {
                            childrenIds.push(selectNodes[j].id);
                            childrenNames.push(selectNodes[j].name);
                        }
                    }
                    if (childrenIds.length == 0) {
                        continue;
                    }
                    ids.push(nodes[i].organId + "-" + childrenIds.join(","));
                    names.push(nodes[i].name + "\r\n" + childrenNames.join(","));
                }
                data.selectIds = ids.join("|");
                data.selectNames = names.join("\r\n");
            }
            return data;
        }

        var organIdAndCatIdMap = {};
        if (selectIds) {// js判断选中的节点
            var arrays = selectIds.split("\\|");
            for (var i = 0, l = arrays.length; i < l; i++) {
                var organIdAndCatIds = arrays[i].split("-");
                organIdAndCatIdMap[organIdAndCatIds[0]] = organIdAndCatIds[1];
            }
        }

        // 设置
        var ztree_settings = $.extend(true, {}, setting, {
            data: {
                simpleData: {
                    idKey: 'id',
                    pIdKey: 'parentId'
                },
                key: {
                    title: "name"
                }
            },
            async: {
                enable: true,
                url: "/public/catalog/getOrganCatalogTree",
                autoParam: ["id=parentId", "organId"],
                otherParam: ["catalog", "true", "all", "false"],
                dataFilter: function (treeId, parentNode, responseData) {
                    for (var i = 0, l = responseData.length; i < l; i++) {
                        var node = responseData[i];
                        node.nocheck = true;
                        if (!node.isParent) {// 叶子节点
                            node.isHidden = true;
                            if (node.type == type && node.id != id) {// 类型必须一致，不能选择自己
                                node.nocheck = false;
                                node.isHidden = false;
                                var catIds = organIdAndCatIdMap[node.organId];
                                if (catIds && catIds.indexOf(node.id) > -1) {//单位必须匹配上
                                    node.checked = true;//
                                    
                                }
                            }
                        }
                    }
                    return responseData;
                }
            }
        });
        // 添加模拟滚动条
        //App.initContentScroll(null, "#" + treeId);
        cur.public_catalog_tree = $.fn.zTree.init($("#" + treeId), ztree_settings);
        return cur.public_catalog_tree;
    }

    return {
        init: init
    }
}();