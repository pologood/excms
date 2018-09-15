var user_rights_tree = function () {
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
                idKey: "indicatorId",
                pIdKey: "parentId"
            }
        }
    };

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    /*function saveOpt() {
        var checkedNodes = cur.userOptTreeObj.getCheckedNodes(true);
        var strJson = [];
        for (var i = 0; i < checkedNodes.length; i++) {
            strJson.push({organId: cur.organId, userId: cur.userId, siteId: checkedNodes[i].indicatorId});
        }
        var data = {};
        data.strJson = mini.encode(strJson);
        data.organId = cur.organId;
        data.userId = cur.userId;
        Ls.ajax({
            url: "/role/site/opt/saveUserOpt",
            data: data,
            success: function (resp) {
                Ls.tips(resp.desc, {times: 2});
                cur.win.reloadGrid();
            }
        });
    }*/

    var siteOpt = function () {

        function dataFilter(treeId, parentNode, responseData) {
            var responseData = Ls.treeDataFilter(responseData, Ls.treeDataType.SITE);
            return responseData;
        };

        function onCheck(event, treeId, node) {
            cur.userOptTreeObj.expandNode(node, true, true, true);
            var nodes = cur.userOptTreeObj.transformToArray(node);
            for (var i = 0; i < nodes.length; i++) {
                $("input[id*=" + nodes[i].indicatorId + "]").prop("checked", nodes[i].checked);
                var functions = nodes[i].functions;
                if (functions != null) {
                    for (var j = 0; j < functions.length; j++) {
                        if (functions[j] != null) {
                            functions[j].checked = nodes[i].checked;
                        }
                    }
                }
            }
            //saveOpt();
        }

        function onClick(event, treeId, node) {
            if (node.isParent) {
                node.expand = node.open;
                cur.userOptTreeObj.expandNode(node, !node.expand, true, true);
                node.open = !node.expand;
            }

            cur.userOptTreeObj.checkNode(node, !node.checked, true);
            //saveOpt();
        }

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont
            },
            check: {
                enable: true,
                chkboxType: { "Y": "p"}
            },
            async: {
                url: "/role/site/opt/getUserSiteOpt?organId=" + cur.organId + "&userId=" + cur.userId,
                dataFilter: dataFilter
            },
            callback: {
                onClick: onClick,
                onCheck: onCheck,
                onAsyncSuccess: function () {
                    var nodes = cur.userOptTreeObj.getNodes();
                    if (nodes.length > 0) {
                        cur.userOptTreeObj.expandNode(nodes[0], true, false, true);
                    }
                }
            }
        });

        cur.userOptTreeObj = $.fn.zTree.init($("#user_opt_tree"), settings);
    }
    return {
        siteOpt: siteOpt
    }

}();