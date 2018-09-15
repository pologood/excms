var msg_classify_tree = function () {

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
            }
        }
    };

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    var init = function () {

        function addDiyDom(treeId, node) {
        }

        function dataFilter(treeId, parentNode, responseData) {

            var data = Ls.treeDataFilter(responseData,Ls.treeDataType.SUBMITTED);

            for(var i=0;i<data.length;i++) {
                data[i].isParent = false;
                data[i].pid = -1;
            }
            //添加根节点
            data.push({
                "id": -1,
                "name": "信息类别",
                "template":false,
                "font": {
                    "font-weight": "bold",
                    "color": "#666666"
                },
                "isParent": true
            })

            return responseData;
        };

        function onClick(event, treeId, node) {
            if(node.id != -1) {
                cur.classifyId = node.id;
                search();
            }
        }

        function onExpand(event, treeId, treeNode) {
        }

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont,
                addDiyDom: addDiyDom
            },
            async: {
                url: '/msg/submit/classify/getEOs',
                dataFilter: dataFilter
            },
            callback: {
                onClick: onClick,
                onAsyncSuccess: function () {
                    var nodes = cur.treeObj.getNodes();
                    if (nodes.length > 0) {
                        cur.treeObj.expandNode(nodes[0], true, false, true);
                    }
                    //添加模拟滚动条
                    initSlimScroll();
                }
            }
        });

        cur.treeObj = $.fn.zTree.init($("#ui_tree"), settings);

    }

    function initSlimScroll() {
        //添加模拟滚动条
        /*var ui_tree = $('#ui_tree');
        var ui_layout = $(".mini-layout-region-body");
        ui_tree.attr("data-height", ui_layout.height() - 10)
        App.initSlimScroll(ui_tree);*/
        App.initContentScroll($(".mini-layout-region-body").height() - 10, "#ui_tree");
    }

    return {
        init: init
    }

}();