/** 移动新闻 */
var move_mgr = function () {
    // 公共属性
    var setting = {
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

    // 栏目树，跨站点，传入的siteId、columnId是当前操作的文章所属站点和栏目
    // 当为引用时，需要查询当前文章的引用关系
    var columnInit = function (treeId, columnType, siteId, columnId, contentId, type) {
        // 设置
        var ztree_settings = $.extend(true, {}, setting, {
            data: {
                simpleData: {
                    idKey: 'columnStrId',
                    pIdKey: 'parentStrId'
                }
            },
            view: {
                addDiyDom: function (id, node) {
                    if (!node.isParent) {
                        node.isPublish = false;
                        var isPublish = false;//判断是否有发布权限
                        if (node.opt && node.opt == "super") {
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
                            var thisId = "isPublish_" + node.columnStrId;
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
            }
        });
        // 加载树
        var tree = null;
        var json = Ls.ajax({
            async: false,// 同步
            url: Ls.getWin.win.GLOBAL_CONTEXTPATH + "/siteMain/getMoveColumnTreeByType?columnTypeCode=" + columnType
        }).responseJSON;
        // 构造树
        if (json.status == 1) {// 成功
            if (json.data != null && json.data.length > 0) {
                var data = Ls.treeDataFilter(json.data, Ls.treeDataType.SITE);
                var referId = [];// 引用关系列表

                var strId = columnId + "_" + siteId;
                for (var i = 0; i < data.length; i++) {
                    var node = data[i];
                    node.nocheck = true;
                    if (!node.isParent) {// 叶子节点
                        node.isHidden = true;
                        if (columnType == "articleNews" || columnType == "pictureNews"||columnType=="videoNews") {

                            if ((node.columnTypeCode == "articleNews" || node.columnTypeCode == "pictureNews"||node.columnTypeCode=="videoNews")
                                && node.columnStrId != strId) {// 图片新闻和文章新闻可以相互移动，不能选择自己
                                node.nocheck = false;
                                node.isHidden = false;
                            }
                        }
                    }
                }
                tree = $.fn.zTree.init($("#" + treeId), ztree_settings, data);
                // App.initContentScroll(90);
            }
        }
        return tree;
    }
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
                            if (node.type == type && node.id != id) {// 类型必须一致，不能选择自己
                                node.nocheck = false;
                                node.isHidden = false;
                            }
                        }
                    }
                    return responseData;
                }
            }
        });
        return $.fn.zTree.init($("#" + treeId), ztree_settings);
    }
    // 信息报送树，不跨站点
    var msgInit = function (treeId) {
        // 设置
        var ztree_settings = $.extend(true, {}, setting, {
            data: {
                key: {
                    title: "name"
                }
            },
            async: {
                enable: true,
                url: Ls.getWin.win.GLOBAL_CONTEXTPATH + '/msg/submit/classify/getEOs',
                dataFilter: function (treeId, parentNode, responseData) {
                    if (responseData && responseData.length > 0) {
                        Ls.treeDataFilter(responseData, Ls.treeDataType.SUBMITTED);
                        for (var i = 0, l = responseData.length; i < l; i++) {
                            responseData[i].isParent = false;
                            responseData[i].pid = -1;
                        }
                        // 添加根节点
                        responseData.push({
                            "id": -1,
                            "name": "信息类别",
                            "isParent": true,
                            "nocheck": true,
                            "open": true
                        });
                    }
                    return responseData;
                }
            }
        });
        return $.fn.zTree.init($("#" + treeId), ztree_settings);
    }
    // 微信素材
    var weixinInit = function (treeId) {
        // 设置
        var ztree_settings = $.extend(true, {}, setting, {
            data: {
                key: {
                    title: "name"
                }
            }
        });
        // 构建数据
        var data = [{
            "name": "微信素材"
        }];
        return $.fn.zTree.init($("#" + treeId), ztree_settings, data);
    }
    return {
        columnInit: columnInit,
        publicInit: publicInit,
        msgInit: msgInit,
        weixinInit: weixinInit
    }
}();