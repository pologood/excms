var label_mgr = function () {

    var ztree_elem = "label_tree", isLoad = false;

    //加载框架页面
    function loadFormPage(action, pid) {
        App.getContentAjax('/label/' + action).done(function (res) {
            $("#label_body").html(res)
        });
        //Ls.ajaxPage('/label/' + action, '#lebel_wrap');
    }

    //初始化树
    var init = function (searchName) {

        mini.parse();

        var label_tree = $("#" + ztree_elem);
        //树配置
        var setting = {
            isEdit: true,
            view: {
                showTitle: true,
                addDiyDom: addDiyDom,
                dblClickExpand: false
            },
            async: {
                enable: true,
                url: "/label/tree",
                otherParam: {
                    "pid": 0,
                    "searchName": searchName || ''
                },
                autoParam: ["id=pid"],
                dataFilter: function (treeId, parentNode, responseData) {
                    var responseData = Ls.treeDataFilter(responseData, Ls.treeDataType.LABEL);
                    if (!isLoad && isRoot) {
                        var responseData = {
                            "id": 0,
                            "pId": 0,
                            "name": "标签列表",
                            "config": null,
                            "description": null,
                            "isRoot": true,
                            "open": true,
                            "children": responseData
                        }
                        isLoad = true;
                    }

                    return responseData;
                }
            },
            callback: {
                onClick: function (event, treeId, treeNode) {
                    if (treeNode.isParent) {
                        label_mgr.ztree.cancelSelectedNode(treeNode);
                        label_mgr.ztree.expandNode(treeNode, !treeNode.open, true, true);
                    } else {
                        loadFormPage('list?action=show&id=' + treeNode.id);
                    }
                },
                onAsyncSuccess: function (event, treeId, treeNode, msg) {
                    label_mgr.ztree = $.fn.zTree.getZTreeObj(ztree_elem);

                    /*var nodes = label_mgr.ztree.getNodes();
                     if (nodes.length > 0) {
                     label_mgr.ztree.expandNode(nodes[0], true, true, true);
                     }*/

                    //添加模拟滚动条
                    // App.initContentScroll();
                    App.initContentScroll(null, '#label_tree', {right: true});
                }
            }
        };
        $.fn.zTree.init(label_tree, setting);
    }

    //添加树上面的操作按扭
    function addDiyDom(treeId, node) {
        var aObj = $("#" + node.tId + "_a");

        var addStr = "<span class='button add-a' id='addBtn_" + node.id + "' title='添加标签'></span>";
        var editStr = " <span class='button edit-a' id='editBtn_" + node.id + "' title='修改标签'></span>";
        var delStr = "<span class='button del-a' id='delBtn_" + node.id + "' title='删除标签'></span>";
        var copyStr = " <span class='button copy-a' id='copyBtn_" + node.id + "' title='复制标签'></span>";

        if (node.isParent && node.isRoot) {
            aObj.after(addStr);
            var btn = $("#addBtn_" + node.id);
            btn && btn.on("click", function (e) {
                editLabel(node.id, "");
                return false;
            });
        }

        if (!node.isParent && node.isRoot) {

            aObj.after(copyStr);
            var btn = $("#copyBtn_" + node.id);
            btn && btn.on("click", function () {
                copyLabel(node.pId, node.id);
                return false;
            });

            aObj.after(delStr);
            var btn = $("#delBtn_" + node.id);
            btn && btn.on("click", function () {
                if (confirm("删除以后无法恢复，您确信要删除此项目吗？")) {
                    delLabel(node.pId, node.id);
                }
                return false;
            });
        }

        if (node.id != 0 && node.isRoot) {
            aObj.after(editStr);
            var btn = $("#editBtn_" + node.id);
            btn && btn.on("click", function () {
                editLabel(node.pId, node.id);
                return false;
            });

        }

    }

    function editLabel(pid, id) {
        Ls.openWin('/label/edit?id=' + id + '&pid=' + pid, '500px', '230px', {
            title: '标签属性编辑'
        });
    }

    function copyLabel(pid, id) {
        Ls.openWin('/label/edit?id=' + id + '&pid=' + pid + '&actionType=copy', '500px', '230px', {
            title: '复制标签'
        });
    }

//删除标签
    var delLabel = function (pid, id) {
        Ls.ajaxGet({
            url: "/label/delLabel",
            data: {
                id: id
            },
            success: function (d) {
                if (d.status) {
                    var node = label_mgr.ztree.getNodeByParam("id", pid, null);
                    label_mgr.ztree.reAsyncChildNodes(node, "refresh");
                } else {
                    Ls.tipsErr(d.desc);
                }
            }
        });
    }

    return {
        init: init
    }

}();