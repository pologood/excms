/** 移动新闻 */
var move_mgr = function () {
    // 公共属性
    var isInit = false,
        setting = {
            view: {
                nameIsHTML: true,
                selectedMulti: false,
                dblClickExpand: false,
                expandSpeed: "fast"
            },
            check: {
                enable: true,
                chkStyle: "radio",
                radioType: "all"
            },
            data: {
                simpleData: {
                    enable: true,// 简单模式
                }
            },
            callback: {
                onClick: function (event, treeId, node) {
                    var tree = $.fn.zTree.getZTreeObj(treeId);
                    if (node.nocheck) {
                        tree.expandNode(node, !node.open, false, true, true);
                        tree.cancelSelectedNode(node);
                    } else {
                        tree.checkNode(node, !node.checked);
                    }
                }
            }
        };

    // 信息公开树，不跨站点，传入当前操作的节点公开类型和目录id
    var publicInit = function (treeId, type, id) {
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
            view: {
                addDiyDom: function (id, node) {
                    if (!node.isParent && node.type == type) {
                        node.isPublish = false;
                        var isPublish = false;//判断是否有发布权限
                        if (node.superAdmin) {
                            isPublish = true;
                        } else {
                            var functions = node.functions;
                            if (functions && functions.length > 0) {
                                for (var i in functions) {
                                    if (functions[i].action && functions[i].action == "publish") {
                                        isPublish = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (isPublish) {
                            var aObj = $("#" + node.tId + "_a");
                            var thisId = "isPublish_" + node.organId + "_" + node.id;
                            var btn = "<input id='" + thisId + "' type='checkbox'/><label for='" + thisId + "'><span style='color:#da9264'>发布</span></label>";
                            aObj.after(btn);
                            $("#" + thisId).click(function () {
                                if ($(this).is(':checked')) {//判断是否选中
                                    node.isPublish = true;
                                }
                            });
                        }
                    }
                }
            },
            async: {
                enable: true,
                url: Ls.getWin.win.GLOBAL_CONTEXTPATH + "/public/catalog/getOrganCatalogTree",
                autoParam: ["id=parentId", "organId"],
                otherParam: ["catalog", "true", "all", "false"],
                dataFilter: function (treeId, parentNode, responseData) {
                    for (var i = 0, l = responseData.length; i < l; i++) {
                        var node = responseData[i];
                        node.nocheck = true;
                        if (!node.isParent) {// 叶子节点
                            node.isHidden = true;
                            if (node.type == type && !(node.id == id && node.organId == cur.W.cur.node.organId)) {// 类型必须一致，不能选择自己
                                node.nocheck = false;
                                node.isHidden = false;
                            }
                        }
                    }
                    return responseData;
                }
            },
            callback: {
                onAsyncSuccess: function () {
                    // 加载列表
                    if (!isInit) {
                        isInit = true;
                        // 添加模拟滚动条
                        //App.initContentScroll(90);
                    }
                }
            }
        });
        var tree = $.fn.zTree.init($("#" + treeId), ztree_settings);
        return tree;
    }
    return {
        publicInit: publicInit
    }
}();