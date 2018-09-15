/** 复制引用新闻 */
var copyRefer_mgr = function () {
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
                                } else {
                                    node.isPublish = false;
                                }
                            });

                            var synColumnIds = cur.synColumnIds.split(",");
                            var synColumnIsPublishs = cur.synColumnIsPublishs.split(",");
                            for (var i = 0; i < synColumnIds.length; i++) {
                                if (node.columnStrId == synColumnIds[i]) {
                                    if (synColumnIsPublishs[i] == "1") {
                                        $("#" + thisId).click();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        });
        // 加载树
        var tree = null;
        var json = Ls.ajax({
            async: false,// 同步
            url: Ls.getWin.win.GLOBAL_CONTEXTPATH + "/siteMain/getColumnTreeByType?columnTypeCode=" + columnType
        }).responseJSON;
        // 构造树
        if (json.status == 1) {// 成功
            if (json.data != null && json.data.length > 0) {
                var data = Ls.treeDataFilter(json.data, Ls.treeDataType.SITE);
                var referId = [];// 引用关系列表
                if (type == 2) {// 引用
                    var refer = Ls.ajax({
                        async: false,// 同步
                        url: Ls.getWin.win.GLOBAL_CONTEXTPATH + "/referRelation/getRelation",
                        data: {
                            causeId: contentId,
                            modelCode: "CONTENT",
                            type: "REFER"
                        }
                    }).responseJSON;
                    if (refer.status == 1) {
                        var referIds = refer.data;
                        if (referIds != null) {
                            for (var i = 0, l = referIds.length; i < l; i++) {
                                referId.push(referIds[i].columnId);
                            }
                        }
                    }
                }
                var strId = columnId + "_" + siteId;
                var checkedColumnStrId = [];
                for (var i = 0; i < data.length; i++) {
                    var node = data[i];
                    node.nocheck = true;
                    if (!node.isParent) {// 叶子节点
                        node.isHidden = true;
                        if (columnType == "articleNews" || columnType == "pictureNews") {

                            if ((node.columnTypeCode == "articleNews" || node.columnTypeCode == "pictureNews")
                                && node.columnStrId != strId) {// 图片新闻和文章新闻可以相互复制，不能选择自己
                                node.nocheck = false;
                                node.isHidden = false;
                                if (type == 2 && referId.indexOf(node.indicatorId) > -1) {
                                    node.checked = true;
                                    node.chkDisabled = true;
                                }
                            }
                        } else {
                            if (node.columnTypeCode == columnType && node.columnStrId != strId) {// 类型必须一致，不能选择自己
                                node.nocheck = false;
                                node.isHidden = false;
                                if (type == 2 && referId.indexOf(node.indicatorId) > -1) {
                                    node.checked = true;
                                    node.chkDisabled = true;
                                }
                            }
                        }
                    }
                    //新增文章页同步信息回显
                    if (cur.synColumnIds.indexOf(node.columnStrId) >= 0) {
                        node.checked = true;
                        checkedColumnStrId.push(node.columnStrId);
                    }
                }
                tree = $.fn.zTree.init($("#" + treeId), ztree_settings, data);

                if (checkedColumnStrId.length > 0) {//展开选中节点的父节点
                    for (var i = 0, l = checkedColumnStrId.length; i < l; i++) {
                        var node = tree.getNodeByParam("columnStrId", checkedColumnStrId[i], null);
                        tree.selectNode(node);
                    }
                }
                // App.initContentScroll(90);
            }
        }
        return tree;
    }
    // 信息公开树，不跨站点，传入当前操作的节点公开类型和目录id
    var checkedOrgan = [], selectedOrganId = [], selectedCatId = [];
    var publicInit = function (treeId, type, id, organId) {
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
                            var organCatId = node.organId + "_" + node.id;
                            var synOrganCatIds = cur.synOrganCatIds.split(",");
                            var synOrganIsPublishs = cur.synOrganIsPublishs.split(",");
                            for (var i = 0; i < synOrganCatIds.length; i++) {
                                if (organCatId == synOrganCatIds[i]) {
                                    if (synOrganIsPublishs[i] == "1") {
                                        $("#" + thisId).click();
                                    }
                                    break;
                                }
                            }

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
                    checkedOrgan = [], selectedOrganId = [], selectedCatId = [];//每次处理数据时置空，不然定位到节点会出错

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
                        //新增文章页同步信息回显
                        if (node.id == node.organId) {//单位节点
                            if (cur.synOrganCatIds.indexOf(node.organId) >= 0) {
                                checkedOrgan.push(node.organId);
                            }
                        } else {
                            var organCatId = node.organId + "_" + node.id;
                            if (cur.synOrganCatIds.indexOf(organCatId) >= 0) {
                                selectedOrganId.push(node.organId);
                                selectedCatId.push(node.id);
                                node.checked = true;
                            }
                        }
                    }
                    return responseData;
                }
            },
            callback: {
                onAsyncSuccess: function (event, treeId, treeNode) {
                    var treeObj = $.fn.zTree.getZTreeObj(treeId);
                    if (checkedOrgan.length > 0) {//展开选中节点的父节点
                        for (var i = 0; i < checkedOrgan.length; i++) {
                            var node = treeObj.getNodeByParam("id", checkedOrgan[i], null);
                            treeObj.expandNode(node, true, false, false);
                            if (node.isParent && !node.zAsync) {
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
        });
        return $.fn.zTree.init($("#" + treeId), ztree_settings);
    }

    function publictreefilter(node) {
        if (selectedCatId.length > 0) {
            for (var i = 0; i < selectedCatId.length; i++) {
                if (selectedCatId[i] == node.id && selectedOrganId[i] == node.organId) {
                    return true;
                }
            }
        }
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
                            if (cur.synMsgCatIds.indexOf(responseData[i].id) >= 0) {
                                responseData[i].checked = true;
                            }
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