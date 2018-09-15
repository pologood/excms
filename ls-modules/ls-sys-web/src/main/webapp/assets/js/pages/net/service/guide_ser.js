var guide_ser = function () {

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
                cur.treeGuideSerObj.cancelSelectedNode(node);
                cur.treeGuideSerObj.expandNode(node);
            } else {
                cur.treeGuideSerObj.checkNode(node,!node.checked,true);
                search();
            }
            event.stopPropagation();
        }

        function onCheck(event, treeId, node) {
            search();
        }

        function onExpand(event, treeId, treeNode) {
        }

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont
            },
            async: {
                url: "/netClassify/getClassifyEOs",
                dataFilter: dataFilter,
                autoParam: ["id"]
            },
            data: {
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
                onCheck:onCheck,
                onAsyncSuccess: function () {
                    var nodes = cur.treeGuideSerObj.getNodes();
                    if (nodes.length > 0) {
                        cur.treeGuideSerObj.expandNode(nodes[0], true, false, true);
                    }
                    //添加模拟滚动条
                    initSlimScroll();
                }
            }
        });

        cur.treeGuideSerObj = $.fn.zTree.init($("#ui_tree_ser"), settings);

    }

    function initSlimScroll() {
        //添加模拟滚动条
        var ui_tree = $('#ui_tree_ser');
        var ui_layout = $(".mini-layout-region-body");
        ui_tree.attr("data-height", ui_layout.height() - 10);
        App.initSlimScroll(ui_tree);
    }

    return {
        init: init
    }

}();