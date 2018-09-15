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
            enable: false,
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
                idKey: 'indicatorId',
                pIdKey: 'parentId'
            }
        }
    };

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    var init = function () {
        function onClick(event, treeId, node) {
            cur.node = node;
            if(node.isParent){
                cur.treeObj.cancelSelectedNode(node);
                cur.treeObj.expandNode(node);
                gridShow();
            } else {
                cur.treeObj.checkNode(node,!node.checked,true);
                formShow();
            }
            event.stopPropagation();
        }

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont
            },
            data: {
                keep: {
                },
                simpleData: {
                    enable: true
                }
            },
            callback: {
                onClick: onClick,
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
                    initSlimScroll();
                }
            }
        });

        Ls.ajax({
            url : "/column/update/warn/getColumnTree",
            type : "post",
            dataType : "json",
            success : function(resp) {
                var data = Ls.treeDataFilter(resp,Ls.treeDataType.SITE);
                cur.treeObj = $.fn.zTree.init($("#ui_tree"), settings,data);//初始化树节点时，添加同步获取的数据
            }
        });
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