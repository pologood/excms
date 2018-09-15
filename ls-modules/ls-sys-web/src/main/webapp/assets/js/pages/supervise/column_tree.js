var column_tree = function () {

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
            simpleData: {
                enable: true,
                idKey: 'columnStrId',
                pIdKey: 'parentStrId'
            }
        }
    };

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    var init = function () {
        cur.map = new Map();
        function addDiyDom(treeId, node) {
        }

        function dataFilter(treeId, parentNode, responseData) {
            var data = Ls.treeDataFilter(responseData,Ls.treeDataType.SITE);
            for(var i = 0;i<data.length;i++) {
                if(data[i].isParent && data[i].type!="CMS_Site" && data[i].type!="SUB_Site") {
                    // data[i].nocheck = true;
                } else {
                    if(cur.columnIds != null && cur.columnIds != "") {
                        if(cur.columnIds.indexOf(data[i].indicatorId) != -1 && cur.cSiteIds.indexOf(data[i].siteId) != -1) {
                            data[i].checked = true;
                        }
                    }
                }
            }

            return data;
        };

        function onClick(event, treeId, node) {
            if(node.isParent){
                cur.treeObj.cancelSelectedNode(node);
                cur.treeObj.expandNode(node);
            } else {
                cur.treeObj.checkNode(node,!node.checked,true);
                if(node.checked) {
                } else {
                }
            }
            event.stopPropagation();
        }

        function onExpand(event, treeId, treeNode) {
        }

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont
            },
            async: {
                url: "/column/update/getColumnTreeBySite",
                dataFilter: dataFilter,
                otherParam: {taskType:cur.taskType},
                autoParam: ['indicatorId']
            },
            data: {
                keep: {
                },
                simpleData: {
                    enable: true
                }
            },
            check: {
                enable: true,
                chkStyle: "checkbox",
                chkboxType: { "Y": "ps", "N": "s" }
            },
            callback: {
                onClick: onClick,
                onCheck:function(event, treeId, node) {
                    if(node.checked) {
                    } else {
                    }
                },
                onAsyncSuccess: function () {

                    var ckNodes = cur.treeObj.getCheckedNodes(true);
                    if (ckNodes != null) {
                        for(var i=0;i<ckNodes.length;i++) {
                            cur.treeObj.checkNode(ckNodes[i], true, true);
                        }
                    }
                    var nodes = cur.treeObj.getNodes();
                    if (nodes.length > 0) {
                        cur.treeObj.expandNode(nodes[0], true, false, true);
                    }

                    //添加模拟滚动条
                    //initSlimScroll();

                    cur.treeNodes = cur.treeObj.transformToArray(cur.treeObj.getNodes());

                    $.each(cur.treeNodes, function (i, v) {
                        v.value = v.name; // 必要格式or label
                    })

                    $( "#tags" ).autocomplete({
                        source: cur.treeNodes,
                        select: function (e, ui) {
                            var text = $(e.toElement).text();
                            $(this).val(ui.item.name);

                            var node = cur.treeObj.getNodesByParam("indicatorId", ui.item.indicatorId, null)[0];
                            if (!node.isParent) {
                                node = node.getParentNode();
                            }

                            cur.treeObj.expandAll();
                            setTimeout(function () {
                                cur.treeObj.expandNode(node, true, false, true);
                            }, 500)

                            return false;
                        }
                    }).data("ui-autocomplete")._renderItem = function (ul, item) {
                        var getPath = item.getPath(),
                            str = '';

                        $.each(getPath, function (i, v) {
                            str += v.name + '>'
                        })
                        str = str.substring(0, str.lastIndexOf('>'));

                        return $("<li>")
                            .append('<a href="javascript:void(0);">' + str + '</a>')
                            .appendTo(ul);
                    };
                }
            }
        });

        cur.treeObj = $.fn.zTree.init($("#ui_tree"), settings);

    }

    function initSlimScroll() {
        //添加模拟滚动条
        var ui_tree = $('#ui_tree');
        ui_tree.attr("data-height", 260)
        App.initSlimScroll(ui_tree);
    }

    return {
        init: init
    }

}();