// 信息报表指标管理
var publicReportEdit = function () {
    /**设置*/
    var settings = {
        view: {
            addDiyDom: addDiyDom
        },
        data: {
            key: {
                title: "title",
                name: "title"
            },
            simpleData: {
                enable: true,
                idKey: 'id',
                pIdKey: 'parentId'
            }
        },
        callback: {
            onClick: function (event, treeId, node) {
                cur.tree.cancelSelectedNode(node);
                cur.tree.expandNode(node, !node.open, false, false, true);
                return false;
            }
        }
    }

    /**
     * 加载数据
     */
    var publicAjax = function (url, data, callback) {
        Ls.ajax({
            url: url,
            data: data
        }).done(function (d) {
            if (callback) {
                callback(d);
            }
        });
    }

    var addDeleteButton = function (node) {
        if (node.id == "0") {
            return;
        }
        var children = node.children;
        if (!children || children.length == 0) {//要加上刪除按鈕
            var aObj = $("#" + node.tId + "_a");
            var delBtn = '<span class="button del-a" id="delBtn_' + node.id + '" title="' + node.title + '"></span>';
            aObj.find(":last").after(delBtn);
            delBtn = $("#delBtn_" + node.id);
            // 删除
            delBtn && delBtn.on("click", function () {
                deleteNode(node);
                return false;
            });
        }
    }

    /**
     * 删除节点
     */
    var deleteNode = function (node) {
        if (confirm('真的要删除吗？')) {
            Ls.ajaxGet({
                url: "/public/report/delete?id=" + node.id
            }).done(function (d) {
                if (d.status == 1) {
                    Ls.tipsOk('删除成功!');
                    cur.tree.removeNode(node);
                    var parentId = node.parentId;
                    var parentNode = cur.tree.getNodeByParam("id", parentId, null);
                    addDeleteButton(parentNode);
                } else {
                    Ls.tipsErr(d.desc);
                }
            });
        }
    }

    var substringTitle = function (title) {
        if (title && title.length > 17) {
            title = title.substring(0, 17) + "...";
        }
        return title;
    }

    /**
     * 更新目录
     */
    var updatePublicReport = function (data) {
        data.title = substringTitle(data.title);
        if (cur.type == 1) { // 新增
            var parentId = data.parentId;
            var parentNode = cur.tree.getNodeByParam("id", parentId, null);
            cur.tree.addNodes(parentNode, data);
            $("#delBtn_" + parentId).remove();
        } else if (cur.type == 2) { // 修改
            var oldnode = cur.tree.getNodeByParam("id", data.id, null);
            $.extend(oldnode, data);
            cur.tree.updateNode(oldnode);
        }
    }

    /**
     * 目录编辑
     */
    function addDiyDom(treeId, node) {
        var aObj = $("#" + node.tId + "_a");
        var text = $("#" + node.tId + "_span").html();
        $("#" + node.tId + "_span").html(substringTitle(text));
        var addBtn = ' <span class="button add-a" id="addBtn_' + node.id + '" title="' + node.title + '"></span>';
        var editBtn = ' <span class="button edit-a" id="editBtn_' + node.id + '" title="' + node.title + '"></span>';

        // 如果是父节点，不显示删除按扭
        if (node.id != "0") {
            addBtn += editBtn;
        }
        aObj.find(":last").after(addBtn);
        addDeleteButton(node);

        addBtn = $("#addBtn_" + node.id);
        editBtn = $("#editBtn_" + node.id);
        // 添加
        addBtn && addBtn.on("click", function () {
            cur.type = 1;
            cur.tree.selectNode(node, false, true);
            cur.getData(node);
            return false;
        });
        // 修改
        editBtn && editBtn.on("click", function (e) {
            cur.type = 2;
            cur.tree.selectNode(node, false, true);
            cur.getData(node);
            return false;
        });
    }

    function selectKeyIds() {
        Ls.openWin(cur.public_catalog_url, '400px', '400px', {
            id: 'public_report_catalog',
            title: '指标统计项',
            padding: 0,
            ok: function () {
                var iframe = this.iframe.contentWindow;
                var data = iframe.cur.ok();
                if (data != null) {
                    cur.vm.backup1 = data.selectNames;
                    cur.vm.keyIds = data.selectIds;
                }
            }
        });
    }

    function init() {
        // 初始化布局
        mini.parse();

        cur.getData = function (node) {
            $(".portlet").show();
            // 获取参数

            publicAjax(cur.public_report_url, {
                id: cur.type == 1 ? "" : node.id
            }, function (d) {
                if (d.status == 1) {
                    var data = d.data;
                    data.nofill_value = !data.isFill;
                    data.sql_value = data.isFill ? false : data.isSql;
                    data.nosql_value = data.isFill ? false : !data.isSql;

                    data.$id = cur.id;
                    // 判断父子关系
                    if (cur.type == 1) {
                        data.parentId = node.id;

                        // 给默认序号
                        var children = node.children;
                        if (!children || children.length == 0) {
                            data.sortNum = 1;
                        } else {
                            data.sortNum = parseInt(children[children.length - 1].sortNum) + 10;
                        }
                    }
                    // 如果模型已经绑定，不再绑定
                    cur.vm = avalon.vmodels[data.$id];
                    if (!cur.vm) {
                        // 绑定模型
                        cur.vm = avalon.define(data);
                    } else {
                        Ls.assignVM(cur.vm, data);
                    }
                    avalon.scan($("#" + cur.id).get(0), cur.vm);
                    //自定义值
                    cur.vm.$watch("isFill", function (a, b) {
                        cur.vm.nofill_value = b;
                        cur.vm.sql_value = a ? b : a;
                        cur.vm.nosql_value = b;
                    });
                    //自定义sql
                    cur.vm.$watch("isSql", function (a, b) {
                        cur.vm.sql_value = a;
                        cur.vm.nosql_value = b;
                    });
                } else {
                    Ls.tipsErr(d.desc);
                }
            });
        }

        // 提交表单
        $('#' + cur.id).validator({
            fields: {
                'title': '统计指标:required;length(~200)',
                'sortNum': '排序号:required;integer'
            },
            valid: function () {
                publicAjax(cur.save_url, Ls.toJSON(cur.vm.$model), function (d) {
                    if (d.status == 1) {
                        // 关闭弹框
                        Ls.tipsOk("保存成功");
                        updatePublicReport(d.data);
                    } else {
                        Ls.tipsErr(d.desc);
                    }
                });
            }
        });

        publicAjax(cur.public_report_list_url, null, function (d) {
            if (d.status == 1) {
                var data = d.data;
                // 添加根节点
                data.push({
                    id: '0',
                    parentId: '-1',
                    title: '统计指标列表',
                    open: true
                });

                // 树
                cur.tree = $.fn.zTree.init($("#" + cur.public_report_tree_id), settings, data);
                cur.tree.expandAll(true);
                // 添加模拟滚动条
                App.initContentScroll(null, "#" + cur.public_report_tree_id);
            } else {
                Ls.tipsErr(d.desc);
            }
        });
    }

    return {
        init: init,
        selectKeyIds: selectKeyIds
    }
}();