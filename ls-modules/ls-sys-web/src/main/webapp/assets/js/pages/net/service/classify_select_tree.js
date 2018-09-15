var classify_select_tree = function () {

    var ztree_settings = {
        view: {
            nameIsHTML: true,
            showTitle: true,
            selectedMulti: false,
            dblClickExpand: false,
            showLine : false,
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
                title: "name"
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

    var init = function () {
        cur.map = new Map();
        function dataFilter(treeId, parentNode, responseData) {
            var data = Ls.treeDataFilter(responseData,Ls.treeDataType.NET_WORK);
            if(null != data) {
                for(var i=0;i<data.length;i++) {
                    var node = data[i];
                    if(node.indicatorId == cur.columnId) {
                        node.chkDisabled = true;
                        cur.columnName = node.name;
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
            }
            event.stopPropagation();

            if(!node.isParent) {
            }
        }

        function onExpand(event, treeId, treeNode) {
        }

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont
            },
            async: {
                url: "/resources/getClassifyEOsByCIds",
                dataFilter: dataFilter,
                otherParam: {cIds:cur.cIds,typeCode:cur.typeCode}
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
                chkboxType: { "Y": "ps", "N": "ps" }
            },
            callback: {
                onClick: onClick,
                beforeCheck:function(treeId, treeNode) {
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
                }
            }
        });

        cur.treeObj = $.fn.zTree.init($("#ui_tree"), settings);

    }

    function initSlimScroll() {
        //添加模拟滚动条
        var ui_tree = $('#ui_tree');
        var ui_layout = $(".mini-layout-region-body");
        ui_tree.attr("data-height", ui_layout.height() - 30);
        App.initSlimScroll(ui_tree);
    }

    return {
        init: init
    }

}();