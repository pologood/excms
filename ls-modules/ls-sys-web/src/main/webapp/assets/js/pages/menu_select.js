var menu_select = function () {
    var ztree_settings = {
        view: {
            nameIsHTML: true,
            showTitle: true,
            selectedMulti: false,
            dblClickExpand: false,
            expandSpeed: "fast"
        },
        async: {
            enable: true,
            type: "GET",
            dataType: "JSON",
            url: "",
            otherParam: {},
            autoParam: ""
        },
        callback: {},
        data: {
            key: {
                title: ""
            },
            simpleData: {
                enable: true,
                idKey: "id",
                pIdKey: "pId",
                rootPId: 0
            }
        }
    };

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    var menu = function () {

        function dataFilter(treeId, parentNode, responseData) {
            var responseData = Ls.treeDataFilter(responseData, Ls.treeDataType.MENU);
            return responseData;
        };

        function onClick(event, treeId, node) {
            if (node.isParent) {
                node.expand = node.open;
                cur.menuTreeObj.expandNode(node, !node.expand, true, true);
                node.open = !node.expand;
            } else {
                cur.menuTreeObj.checkNode(node, !node.checked, true);
            }
        }

        function onCheck(event, treeId, node) {
            if (cur.roleId == null || cur.roleId == '') {
                Ls.tips("请选择角色!", {time: 2});
            }

            var nodes = cur.menuTreeObj.transformToArray(node);
            for (var i = 0; i < nodes.length; i++) {
                $("input[id*=" + nodes[i].id + "]").prop("checked", nodes[i].checked);
                var rights = nodes[i].rights;
                if (rights != null) {
                    for (var j = 0; j < rights.length; j++) {
                        if (rights[j] != null) {
                            rights[j].checked = true;
                        }
                    }
                }
            }
        }

        function onExpand(event, treeId, treeNode) {
        }

        function checkNodeByTId(node) {
            cur.menuTreeObj.checkNode(node, true, true);
        }

        function addOpt(treeId, treeNode) {

            if (treeNode.checked) {
                checkNodeByTId(treeNode.tId);
            }

            var ids = [];
            var sObj = $("#" + treeNode.tId + "_a");
            var rights = treeNode.rights;
            var opt = "";
            if (rights != null) {
                for (var i = 0; i < rights.length; i++) {
                    var id = treeNode.id + '_' + rights[i].code;
                    ids.push(id);
                    var checked = rights[i].checked;
                    if (checked) {
                        opt = opt + '&nbsp<label><span><input id="' + id + '" value="' + treeNode.tId + '" type="checkbox" right="' + rights[i].code + '" checked><span style="color: #da9264"> ' + rights[i].name + '</span></label></span>';
                        continue;
                    }
                    opt = opt + '&nbsp<label><span><input id="' + id + '" value="' + treeNode.tId + '" type="checkbox" right="' + rights[i].code + '"><span style="color: #da9264"> ' + rights[i].name + '</span></label></span>';
                }
            }
            sObj.after(opt);

            for (var j = 0; j < ids.length; j++) {
                var ckEvt = $("#" + ids[j] + "");
                ckEvt.on('click', function () {
                    var menu_tree_tid = $(this).val();
                    var checked = $(this).is(':checked');
                    var node = cur.menuTreeObj.getNodeByTId(menu_tree_tid);
                    if (checked) {
                        checkNodeByTId(node);
                    }

                    var right = $(this).attr("right");
                    var nodes = cur.menuTreeObj.transformToArray(node);
                    for (var i = 0; i < nodes.length; i++) {
                        var rights = nodes[i].rights;
                        if (rights == null) {
                            continue;
                        }
                        for (var j = 0; j < rights.length; j++) {
                            if (rights[j] != null) {
                                if (rights[j].code == right) {
                                    rights[j].checked = checked;
                                }
                            }
                        }
                        $("input[id*=" + nodes[i].id + "_" + right + "]").prop("checked", checked);
                    }
                });
            }
        }

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont,
                addDiyDom: addOpt
            },
            check: {
                enable: true
            },
            async: {
                url: "/role/menu/getCheckMenu?roleId=" + cur.roleId,
                dataFilter: dataFilter
            },
            callback: {
                onClick: onClick,
                onCheck: onCheck,
                onAsyncSuccess: function () {
                    var nodes = cur.menuTreeObj.getNodes();
                    if (nodes.length > 0) {
                        //cur.menuTreeObj.expandNode(nodes[0], true, false, true);
                        //cur.menuTreeObj.selectNode(nodes[0].children[0]);
                    }

                    initSlimScroll();
                }
            }
        });

        function initSlimScroll() {
            //添加模拟滚动条
            var ui_tree = $('#menu_tree_wrap');
            var ui_layout = $(".mini-layout-region-body");
            ui_tree.attr("data-height", ui_layout.height() - 180);
            App.initSlimScroll(ui_tree);
        }

        cur.menuTreeObj = $.fn.zTree.init($("#menu_tree"), settings);
        cur.menuTreeObj.expandAll(true);

    }
    return {
        menu: menu
    }

}();