var refer_organ_cats = function () {
    var checkedOrgan = [], selectedOrganId = [], selectedCatId = [];
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
                // var tree = $.fn.zTree.getZTreeObj(treeId);
                if (node.nocheck) {
                    cur.public_catalog_tree.expandNode(node, !node.open, false, true, true);
                    cur.public_catalog_tree.cancelSelectedNode(node);
                } else {
                    cur.public_catalog_tree.checkNode(node, !node.checked);
                }
            },
            onAsyncSuccess: function (event, treeId, treeNode) {
                var treeObj = $.fn.zTree.getZTreeObj(treeId);
                if (checkedOrgan.length > 0) {//展开选中节点的父节点
                    for (var i = 0; i < checkedOrgan.length; i++) {
                        var node = treeObj.getNodeByParam("id", checkedOrgan[i], null);
                        treeObj.expandNode(node, true, false, false);
                        if (node.isParent && !node.zAsync ) {
                            treeObj.reAsyncChildNodes(node, "refresh", true);
                        }
                    }
                } else {//定位到选中的子节点
                    if (selectedCatId.length > 0) {
                        var nodes = treeObj.getNodesByFilter(publictreefilter);
                        for (var i = 0; i < nodes.length; i++) {
                            treeObj.selectNode(nodes[i]);
                        }
                    }
                }
            }
        }
    };

    function publictreefilter(node) {
        if (selectedCatId.length > 0) {
            for (var i = 0; i < selectedCatId.length; i++) {
                if (selectedCatId[i] == node.id && selectedOrganId[i] == node.organId) {
                    return true;
                }
            }
        }
    }

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
                            childrenIds.push(nodes[i].organId + "_" +selectNodes[j].id);
                            childrenNames.push(selectNodes[j].name);
                        }
                    }
                    if (childrenIds.length == 0) {
                        continue;
                    }
                    ids.push(childrenIds.join(","));
                    names.push(nodes[i].name + "\r\n" + childrenNames.join(","));
                }
                data.selectIds = ids.join(",");
                data.selectNames = names.join("\r\n");
            }
            return data;
        }

        // var organIdAndCatIdMap = {};
        // if (selectIds) {// js判断选中的节点
        //     var arrays = selectIds.split(",");
        //     for (var i = 0, l = arrays.length; i < l; i++) {
        //         var organIdAndCatIds = arrays[i].split("_");
        //         organIdAndCatIdMap[organIdAndCatIds[0]] += organIdAndCatIds[1]+",";
        //     }
        // }

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
                    checkedOrgan = [], selectedOrganId = [], selectedCatId = [];//每次处理数据时置空，不然定位到节点会出错
                    for (var i = 0, l = responseData.length; i < l; i++) {
                        var node = responseData[i];
                        node.nocheck = true;
                        if (!node.isParent) {// 叶子节点
                            node.isHidden = true;
                            if (node.type == type && node.id != id) {// 类型必须一致，不能选择自己
                                node.nocheck = false;
                                node.isHidden = false;
                            }
                        }
                        //新增文章页同步信息回显
                        if (node.id == node.organId) {//单位节点
                            if (selectIds.indexOf(node.organId) >= 0) {
                                checkedOrgan.push(node.organId);
                            }
                        } else {
                            var organCatId = node.organId + "_" + node.id;
                            if (selectIds.indexOf(organCatId) >= 0) {
                                selectedOrganId.push(node.organId);
                                selectedCatId.push(node.id);
                                node.checked = true;
                            }
                        }
                    }
                    console.log(selectedCatId);
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