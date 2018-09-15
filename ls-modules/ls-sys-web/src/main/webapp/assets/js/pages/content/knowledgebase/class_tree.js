var class_select_tree = function () {
    var init = function (id, selectIds) {
        var setting = {
            view: {
                nameIsHTML: true,
                showTitle: true,
                selectedMulti: false,
                dblClickExpand: false,
                expandSpeed: "fast",
                showIcon: false,
                showLine: false
            },
            check: {
                enable: true,
                nocheckInherit: false
            },
            async: {
                enable: true,
                type: "GET",
                dataType: "JSON",
                url: "/knowledgeBase/getClass",
                otherParam: {},
                dataFilter: dataFilter
            },
            callback: {
                onCheck: onCheck,
                onClick: onClick,
                onAsyncSuccess: onCheck
            },
            data: {
                keep: {},
                key: {
                    title: "name"
                },
                simpleData: {
                    enable: true,
                    idKey: "id",
                    pIdKey: "parentId"
                }
            }
        };

        function dataFilter(treeId, parentNode, responseData) {
            var arr = [];

            if (selectIds && selectIds != "") {
                arr = selectIds.split(",");
            }
            for (var i = 0, l = responseData.length; i < l; i++) {
                var node = responseData[i];
                // 设置父节点不给checkbox
                if (node.parentId == 0 || !node.parentId) {
                    if (!cur.class_select_tree_root_ids) {
                        cur.class_select_tree_root_ids = [];
                    }
                    if ($.inArray(node.id, cur.class_select_tree_root_ids) == -1) {
                        cur.class_select_tree_root_ids.push(node.id);
                    }
                    node.nocheck = true;
                } else {
                    // 设置选中
                    if (arr.indexOf("" + node.id) > -1 || (cur.contentId == "" && node.isSelect)) {
                        node.checked = true;
                    }
                }
            }
            return Ls.treeDataFilter(responseData, Ls.treeDataType.CATALOG);
        }

        function onClick(e, treeId, node) {
            var tree = $.fn.zTree.getZTreeObj(treeId);
            if (node.nocheck) {
                tree.expandNode(node);
                tree.cancelSelectedNode(node);
            } else {
                tree.checkNode(node, !node.checked);
                tree.setting.callback.onCheck(null, treeId, node);
            }
        }

        function onCheck(e, treeId, node) {
            var tree = $.fn.zTree.getZTreeObj(treeId);
            var checks = tree.getCheckedNodes(true);
            // 为空
            $("#" + id + "Ids").val("");
            $("#" + id + "ParentIds").val("");
            $("#" + id + "Names").val("");
            if (checks.length > 0) {
                var ids = [], pIds = [], names = [];
                for (var i = 0, l = checks.length; i < l; i++) {
                    ids.push(checks[i].id);
                    if ($.inArray(checks[i].parentId, pIds) == -1) {
                        pIds.push(checks[i].parentId);
                    }
                    names.push(checks[i].name);
                }
                $("#" + id + "Ids").val(ids.join(","));
                $("#" + id + "ParentIds").val(pIds.join(","));
                $("#" + id + "Names").val(names.join(","));
            }
        }

        $.fn.zTree.init($("#" + id + "_tree"), setting);
    }
    return {
        init: init
    }
}();

function showTree(id, top, heigth) {
    cur.select_tree_id = id;// 设置当前下拉树的id
    var _top = 34;
    var obj = $("#" + id + "Names");
    var icon_btn = $("#" + id + "_btn");
    var offset = obj.offset();
    $("#" + id + "_content").css({
        width: obj.outerWidth() + icon_btn.outerWidth() - 1
    }).show();
    $("body").bind("mousedown", onBodyDown);
}

function hideTree() {
    $("#" + cur.select_tree_id + "_content").hide();
    $("body").unbind("mousedown", onBodyDown);
}

function onBodyDown(event) {
    if (!(event.target.id == cur.select_tree_id + "_btn" || $(event.target).parent().attr("id") == cur.select_tree_id + "_btn" || event.target.id == cur.select_tree_id + "Names" || event.target.id == cur.select_tree_id + "_content" || $(event.target).parents("#" + cur.select_tree_id + "_content").length > 0)) {
        hideTree();
    }
}