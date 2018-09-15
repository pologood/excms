var guide_edit_tree = function () {

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
                title: "name"
            },
            simpleData: {
                enable: true
            }
        }
    };

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    var init = function () {
        cur.map = new Map();
        function dataFilter(treeId, parentNode, responseData) {
            var data = Ls.treeDataFilter(responseData,Ls.treeDataType.ROLE);
            return data;
        };

        function onClick(event, treeId, node) {
            if(node.isParent){
                cur.treeClassifyObj.cancelSelectedNode(node);
                cur.treeClassifyObj.expandNode(node);
            } else {
                cur.treeClassifyObj.checkNode(node,!node.checked,true);
            }
            event.stopPropagation();

            /*if(!node.isParent) {
                var tempNode = node;
                while(node.pid != 0) {
                    node = cur.treeObj.getNodeByParam("id", node.pid, null);
                }

                cur.treeObj.checkNode(node,false,true);

                cur.treeObj.checkNode(tempNode,!tempNode.checked,true);
            }*/
        }

        function onExpand(event, treeId, treeNode) {
        }

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont
            },
            async: {
                url: "/tableResources/getClassifyEOs",
                dataFilter: dataFilter,
                otherParam: { "pId":cur.model.id},
                autoParam: ["id"]
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
                    /*var node = treeNode;
                    while(node.pid != 0) {
                        node = cur.treeClassifyObj.getNodeByParam("id", node.pid, null);
                    }

                    cur.treeClassifyObj.checkNode(node,false,true);*/
                    if (treeNode.isParent) {
                        return false;
                    }
                },
                onAsyncSuccess: function () {
                    var nodes = cur.treeClassifyObj.getNodes();
                    if (nodes.length > 0) {
                        //cur.treeClassifyObj.expandNode(nodes[0], true, false, true);
                    }
                    //添加模拟滚动条
                    initSlimScroll();
                }
            }
        });

        cur.treeClassifyObj = $.fn.zTree.init($("#ui_tree_classify"), settings);

    }

    function initSlimScroll() {
        //添加模拟滚动条
        var ui_tree = $('#ui_tree_classify');
        //var ui_layout = $(".mini-layout-region-body");
        ui_tree.attr("data-height", 400);
        App.initSlimScroll(ui_tree);
    }

    return {
        init: init
    }

}();