// 信息公开统计分析
var publicAnalysisManage = function () {
    // id数据
    var divId = {
        organRanking_datagrid: "organRanking_datagrid",
        applyRanking_datagrid: "applyRanking_datagrid",
        replyRanking_datagrid: "replyRanking_datagrid",
        replyStatusRanking_datagrid: "replyStatusRanking_datagrid",
        catalogRanking_datagrid: "catalogRanking_datagrid",
        lawEnforcementRanking_datagrid: "lawEnforcementRanking_datagrid",
        publicReport_datagrid: "publicReport_datagrid"
    }
    // 统计的字段
    var summaryField = ["total", "applyCount", "replyCount", "catTotal", "statusTotal"];

    var loadPublicTopCatalogList = function (selectId) {
        if (!divId.public_top_catalog_list_str) {
            // 加载目录
            Ls.ajaxGet({
                url: "/public/catalog/getTopCatalogList"
            }).done(function (response) {
                var str = '<option value="">请选择目录</option>';
                if (response != null) {
                    var data = response;
                    var length = data.length;
                    if (length > 0) {
                        for (i = 0; i < length; i++) {
                            str += '<option value=' + data[i].id + '>' + data[i].name + '</option> '
                        }
                    }
                }
                divId.public_top_catalog_list_str = str;
                $("#" + selectId).empty().append(str).val("");
            });
        } else {
            $("#" + selectId).empty().append(divId.public_top_catalog_list_str).val("");
        }
    }

    var init = function (edit) {
        // 初始化布局
        mini.parse();
        // 获取datagrid
        cur.organRanking_datagrid = mini.get(divId.organRanking_datagrid);
        cur.applyRanking_datagrid = mini.get(divId.applyRanking_datagrid);
        cur.replyRanking_datagrid = mini.get(divId.replyRanking_datagrid);
        cur.replyStatusRanking_datagrid = mini.get(divId.replyStatusRanking_datagrid);
        cur.catalogRanking_datagrid = mini.get(divId.catalogRanking_datagrid);
        cur.lawEnforcementRanking_datagrid = mini.get(divId.lawEnforcementRanking_datagrid);
        cur.publicReport_datagrid = mini.get(divId.publicReport_datagrid);
        // 加载数据
        cur.organRanking_datagrid.load();
        // 设置高度
        Ls.mini_datagrid_height(cur.organRanking_datagrid, 90);
        Ls.mini_datagrid_height(cur.applyRanking_datagrid, 90);
        Ls.mini_datagrid_height(cur.replyRanking_datagrid, 90);
        Ls.mini_datagrid_height(cur.replyStatusRanking_datagrid, 90);
        Ls.mini_datagrid_height(cur.catalogRanking_datagrid, 90);
        Ls.mini_datagrid_height(cur.lawEnforcementRanking_datagrid, 90);
        Ls.mini_datagrid_height(cur.publicReport_datagrid, 90);

        /*
         * 解决tab切换时组建加载不全问题
         */
        $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
            var id = e.currentTarget.hash;
            // if (id == '#tab_1_1' && !cur.organRanking_datagrid.isLoad) {
            // cur.organRanking_datagrid.reload();
            // cur.organRanking_datagrid.isLoad = true;
            // }
            if (id == '#tab_1_2' && !cur.applyRanking_datagrid.isLoad) {
                cur.applyRanking_datagrid.reload();
                cur.applyRanking_datagrid.isLoad = true;
            } else if (id == '#tab_1_3' && !cur.replyRanking_datagrid.isLoad) {
                cur.replyRanking_datagrid.reload();
                cur.replyRanking_datagrid.isLoad = true;
            } else if (id == '#tab_1_4' && !cur.replyStatusRanking_datagrid.isLoad) {
                cur.replyStatusRanking_datagrid.isLoad = true;
                // 加载回复状态
                Ls.ajaxGet({
                    url: "/public/apply/getReplyStatus"
                }).done(function (response) {
                    var str = '<option value="">请选择回复状态</option>';
                    if (response != null) {
                        var data = response;
                        var length = data.length;
                        if (length > 0) {
                            cur.replyStatusList = data;
                            for (i = 0; i < length; i++) {
                                str += '<option value=' + data[i].code + '>' + data[i].key + '</option> ';
                                summaryField.push(data[i].code);
                            }
                        }
                    }
                    $("#replyStatusRanking_replyStatusSelect").empty().append(str).val("");
                    // 加载数据
                    search($("#replyStatusRankingForm"), 4);
                });
            } else if (id == '#tab_1_5' && !cur.catalogRanking_datagrid.isLoad) {
                // cur.catalogRanking_datagrid.reload();
                cur.catalogRanking_datagrid.isLoad = true;
                // 加载目录
                Ls.ajaxGet({
                    url: "/public/catalog/getTopCatalogList"
                }).done(function (response) {
                    var str = '<option value="">请选择目录</option>';
                    if (response != null) {
                        var data = response;
                        var length = data.length;
                        if (length > 0) {
                            for (i = 0; i < length; i++) {
                                str += '<option value=' + data[i].id + '>' + data[i].name + '</option> '
                            }
                        }
                    }
                    $("#catalogRanking_catalogSelect").empty().append(str).val("");
                });
            } else if (id == '#tab_1_6' && !cur.lawEnforcementRanking_datagrid.isLoad) {
                // cur.catalogRanking_datagrid.reload();
                cur.lawEnforcementRanking_datagrid.isLoad = true;
                search($("#lawEnforcementRankingForm"), 6);
            } else if (id == '#tab_1_7' && !cur.publicReport_datagrid.isLoad) {
                cur.publicReport_datagrid.isLoad = true;
                cur.publicReport_datagrid.reload();
                loadPublicTopCatalogList("public_report_catalogSelect");
            }
        });
    }

    var onDrawSummaryCell = function (e) {
        var rows = e.data;
        var field = e.field;
        if ($.inArray(field, summaryField) > -1) {
            var total = 0;
            for (var i = 0, l = rows.length; i < l; i++) {
                var v = rows[i][field];
                if (v) {
                    total += v;
                }
            }
            e.cellHtml = "总计: " + total;
        }
    }

    var rendererEmpty = function (e) {
        var v = e.record[e.field];
        if (v) {
            return v;
        }
        return "0";
    }

    var search = function (form, type) {
        var json = $(form).serializeObject();
        if (json.startDate && json.startDate != "") {
            json.startDate += " 00:00:00";
        }
        if (json.endDate && json.endDate != "") {
            json.endDate += " 23:59:59";
        }
        if (type == '1') {
            cur.organRanking_datagrid.load(json);
        } else if (type == '2') {
            cur.applyRanking_datagrid.load(json);
        } else if (type == '3') {
            cur.replyRanking_datagrid.load(json);
        } else if (type == '4') {
            var columns = [{
                width: "60",
                type: "indexcolumn"
            }, {
                field: "organName",
                width: "200",
                align: "center",
                headerAlign: "center",
                header: "单位名称"
            }];
            // 回复状态
            var status = $("#replyStatusRanking_replyStatusSelect").val();
            if (status == "") {// 查询全部
                if (cur.replyStatusList) {
                    var length = cur.replyStatusList.length;
                    for (var i = 0; i < length; i++) {
                        columns.push({
                            field: cur.replyStatusList[i].code,
                            width: 120,
                            align: "center",
                            headerAlign: "center",
                            header: cur.replyStatusList[i].key,
                            renderer: rendererEmpty
                        });
                    }
                }
            } else {
                columns.push({
                    field: status,
                    width: 160,
                    align: "center",
                    headerAlign: "center",
                    header: $("#replyStatusRanking_replyStatusSelect option:selected").text(),
                    renderer: rendererEmpty
                });
            }
            columns.push({
                field: "statusTotal",
                width: 140,
                align: "center",
                headerAlign: "center",
                header: "总数"
            });
            cur.replyStatusRanking_datagrid.setColumns(columns);
            cur.replyStatusRanking_datagrid.load(json);
        } else if (type == '5') {
            if (cur.select_tree_names_array && cur.select_tree_names_array.length > 0) {
                // 动态添加列
                var columns = [{
                    width: "60",
                    type: "indexcolumn"
                }, {
                    field: "organName",
                    width: "200",
                    align: "center",
                    headerAlign: "center",
                    header: "单位名称"
                }, {
                    field: "catTotal",
                    width: "120",
                    align: "center",
                    headerAlign: "center",
                    header: "总数"
                }];
                for (var i = 0; i < cur.select_tree_names_array.length; i++) {
                    columns.push({
                        field: "value" + i,
                        width: 160,
                        align: "center",
                        headerAlign: "center",
                        header: cur.select_tree_names_array[i]
                    });
                }
                cur.catalogRanking_datagrid.setColumns(columns);
                cur.catalogRanking_datagrid.load(json);
            } else {
                Ls.tipsErr("请选择信息公开目录！");
            }
        } else if (type == '6') {
            var columns = [{
                width: "60",
                type: "indexcolumn"
            }, {
                field: "organName",
                width: "200",
                align: "center",
                headerAlign: "center",
                header: "单位名称"
            }, {
                field: "catTotal",
                width: "80",
                align: "center",
                headerAlign: "center",
                header: "总数"
            }];
            // 行政执法目录
            var lawEnforcementIdArr = [{id: 2667887, childrens: [2667933, 2667934], name: "行政审批"}, {
                id: 2667888,
                childrens: [2667935, 2667936],
                name: "行政处罚"
            },
                {id: 2667937, childrens: [2667938, 2667939], name: "行政征收"}, {id: 2667940, childrens: [2667941, 2667942], name: "行政给付"},
                {id: 2667943, childrens: [2667944, 2667945], name: "行政奖励"}, {id: 2667946, childrens: [2667947, 2667948], name: "行政强制"},
                {id: 2667949, childrens: [2667950, 2667951], name: "行政确认"}, {id: 2667952, childrens: [2667953, 2667954], name: "行政裁决"},
                {id: 2667955, childrens: [2667956, 2667957], name: "行政规划"}, {id: 2667890, childrens: [2667958, 2667959], name: "其他权利"}];
            var length = lawEnforcementIdArr.length;
            var catIdArr = [];
            var childrensArr = [];
            for (var i = 0; i < length; i++) {
                catIdArr.push(lawEnforcementIdArr[i].id);
                childrensArr.push(lawEnforcementIdArr[i].childrens.join("-"));
                columns.push({
                    field: "value" + i,
                    width: 80,
                    align: "center",
                    headerAlign: "center",
                    header: lawEnforcementIdArr[i].name,
                    renderer: rendererEmpty
                });
            }
            json.catId = 2660369;
            json.catIds = catIdArr.join(",");
            json.childrenIds = childrensArr.join(",");
            cur.json = json;
            cur.lawEnforcementRanking_datagrid.setColumns(columns);
            cur.lawEnforcementRanking_datagrid.load(json);
        } else if (type == '7') {
            cur.publicReport_datagrid.load(json);
        }
    }

    var searchOrgans = function (form, type) {
        if (type == '1') {
            cur.organRanking_datagrid.load({"isOrgans": 1});
        } else if (type == '2') {
            cur.applyRanking_datagrid.load({"isOrgans": 1});
        } else if (type == '3') {
            cur.replyRanking_datagrid.load({"isOrgans": 1});
        } else if (type == '4') {
            cur.replyStatusRanking_datagrid.load({"isOrgans": 1});
        }
    }

    var searchClear = function (form, type) {
        $(form)[0].reset();
        if (type == '1') {
            cur.organRanking_datagrid.load();
        } else if (type == '2') {
            cur.applyRanking_datagrid.load();
        } else if (type == '3') {
            cur.replyRanking_datagrid.load();
        } else if (type == '4') {
            var columns = [{
                width: "60",
                type: "indexcolumn"
            }, {
                field: "organName",
                width: "200",
                headerAlign: "center",
                header: "单位名称"
            }];
            // 回复状态
            if (cur.replyStatusList) {
                var length = cur.replyStatusList.length;
                for (var i = 0; i < length; i++) {
                    columns.push({
                        field: cur.replyStatusList[i].code,
                        width: 120,
                        headerAlign: "center",
                        header: cur.replyStatusList[i].key,
                        renderer: rendererEmpty
                    });
                }
            }
            columns.push({
                field: "statusTotal",
                width: 160,
                headerAlign: "center",
                header: "总数"
            });
            cur.replyStatusRanking_datagrid.setColumns(columns);
            cur.replyStatusRanking_datagrid.load();
            // cur.replyStatusRanking_datagrid.setColumns(null);
        } else if (type == '5') {
            clearTree();
            $.fn.zTree.destroy("public_catalog_tree");
            cur.catalogRanking_datagrid.setColumns(null);
        } else if (type == '6') {
            search(form, type);//重置
        } else if (type == '7') {
            cur.publicReport_datagrid.load();
        }
    }


    var exportExcel = function (formId, type) {
        var json = $("#" + formId).serializeObject();
        if (json.startDate && json.startDate != "") {
            json.startDate += " 00:00:00";
        }
        if (json.endDate && json.endDate != "") {
            json.endDate += " 23:59:59";
        }
        if (type == '1') {
            $('#download_frame').attr("src", "/public/analysis/exportOrganRanking?" + jsonToGet(json));
        } else if (type == '2') {
            $('#download_frame').attr("src", "/public/analysis/exportApplyRanking?" + jsonToGet(json));
        } else if (type == '3') {
            $('#download_frame').attr("src", "/public/analysis/exportReplyRanking?" + jsonToGet(json));
        } else if (type == '4') {
            $('#download_frame').attr("src", "/public/analysis/exportReplyStatusRanking?" + jsonToGet(json));
        } else if (type == '5') {
            $('#download_frame').attr("src", "/public/analysis/exportCatalogRanking?" + jsonToGet(json));
        } else if (type == '6') {
            var data = cur.json;
            data.startDate = json.startDate;
            data.endDate = json.endDate;
            $('#download_frame').attr("src", "/public/analysis/exportLawEnforcementRanking?" + jsonToGet(data));
        } else if (type == '7') {
            $('#download_frame').attr("src", "/public/report/exportPublicReportList?" + jsonToGet(json));
        }
    }

    // 查询选中的节点
    var childrenFilter = function (node) {
        return node.checked;
    }
    // 清空树
    var clearTree = function () {
        cur.select_tree_names_array = null;
        $("#" + cur.select_tree_id + "_ids").val("");
        $("#" + cur.select_tree_id + "_childrenIds").val("");
        $("#" + cur.select_tree_id + "_names").val("");
    }

    var expandNode = function (node) {
        if (node.isParent && !node.zAsync) {
            cur.catalog_select_tree.reAsyncChildNodes(node, "refresh", true);
        }
    }

    var ztree_settings = {
        async: {
            enable: true,
            url: "/public/catalog/getCatalogsByParentId",
            autoParam: ["id=parentId"]
        },
        data: {
            key: {
                title: "name"
            },
            simpleData: {
                idKey: 'id',
                pIdKey: 'parentId'
            }
        },
        check: {
            enable: true
        },
        callback: {
            onClick: function (event, treeId, node) {
                cur.catalog_select_tree.checkNode(node, !node.checked);
                cur.catalog_select_tree.setting.callback.onCheck(null, treeId, node);
            },
            onCheck: function (event, treeId, node) {
                clearTree();// 清空
                // 展开子节点
                expandNode(node);
                var checks = cur.catalog_select_tree.getCheckedNodes(true);
                if (checks.length > 0) {
                    // if (checks.length > 200) {
                    // 	Ls.tipsErr("最多选择200个信息公开目录！");
                    // 	return;
                    // }
                    var ids = [], childrenIds = [], names = [];
                    for (var i = 0, l = checks.length; i < l; i++) {
                        if (!checks[i].isParent) {
                            ids.push(checks[i].id);
                            // 找到选中的所有子列表
                            var nodes = cur.catalog_select_tree.getNodesByFilter(childrenFilter, false, checks[i]); // 查找节点集合
                            if (nodes && nodes.length > 0) {
                                var childrens = []
                                for (var j = 0, len = nodes.length; j < len; j++) {
                                    childrens.push(nodes[j].id);
                                }
                                childrenIds.push(childrens.join("-"));
                            } else {// 空的话用-9999代替防止出现在后台截取出错
                                childrenIds.push('-9999');
                            }
                            names.push(checks[i].name);
                        }
                    }
                    cur.select_tree_names_array = names;
                    $("#" + cur.select_tree_id + "_ids").val(ids.join(","));
                    $("#" + cur.select_tree_id + "_childrenIds").val(childrenIds.join(","));
                    $("#" + cur.select_tree_id + "_names").val(names.join(","));
                }
            },
            onAsyncSuccess: function (event, treeId, treeNode) {
                cur.catalog_select_tree.expandNode(treeNode, true, false, false);
                if (treeNode.isParent) {
                    var childrens = treeNode.children;
                    if (childrens && childrens.length > 0) {
                        for (var i = 0, l = childrens.length; i < l; i++) {
                            expandNode(childrens[i]);
                        }
                    }
                }
            }
        }
    }

    var changeCatalogTreeSelect = function (select) {
        var id = $(select).val();
        if (id == "" && cur.catalog_select_tree) {
            $.fn.zTree.destroy("public_catalog_tree");
            clearTree();
        } else {
            // 加载目录
            Ls.ajaxGet({
                url: "/public/catalog/getCatalogsByParentId?parentId=" + id
            }).done(function (data) {
                clearTree();
                cur.catalog_select_tree = $.fn.zTree.init($("#public_catalog_tree"), ztree_settings, data);
            });
        }
    }

    var showTree = function (id, top, heigth) {
        if ($("#catalogRanking_catalogSelect").val() == "") {
            Ls.tipsErr("请选择目录！");
            return;
        }
        cur.select_tree_id = id;// 设置当前下拉树的id
        var _top = 34;
        var obj = $("#" + id + "_names");
        var offset = obj.offset();
        $("#" + id + "_content").css({
            width: obj.outerWidth() + 1
        }).show();
        $("body").bind("mousedown", onBodyDown);
    }

    function hideTree() {
        $("#" + cur.select_tree_id + "_content").hide();
        $("body").unbind("mousedown", onBodyDown);
    }

    function onBodyDown(event) {
        if (!(event.target.id == cur.select_tree_id + "_content" || $(event.target).parents("#" + cur.select_tree_id + "_content").length > 0)) {
            hideTree();
        }
    }

    var jsonToGet = function (json) {
        var params = [];
        for (var key in json) {
            params.push(key + "=" + json[key]);
        }
        return params.join("&");
    }

    return {
        init: init,
        search: search,
        searchOrgans: searchOrgans,
        searchClear: searchClear,
        exportExcel: exportExcel,
        onDrawSummaryCell: onDrawSummaryCell,
        changeCatalogTreeSelect: changeCatalogTreeSelect,
        showTree: showTree
    }
}();