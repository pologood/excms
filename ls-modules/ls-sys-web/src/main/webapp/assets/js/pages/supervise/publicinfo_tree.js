var info_open_tree = function () {
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
                title: "id"
            },
            simpleData: {
                enable: true,
                idKey: "id",
                pIdKey: "parentId",
                rootPId: 0
            }
        }
    };

    function checkNodeByTId(node) {
        cur.treeObj.checkNode(node,true,true);
    }

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    var init = function () {

        function dataFilter(treeId, parentNode, responseData) {
            var responseData = Ls.treeDataFilter(responseData,Ls.treeDataType.CATALOG);
            var temp = [];
            for(var i=0;i<responseData.length;i++) {
                if(responseData[i].type == "CMS_Site") {
                    responseData[i].icon= GLOBAL_CONTEXTPATH + "/assets/images/site.png";
                } else {
                    if(cur.columnIds != null && cur.columnIds != "") {
                        if(cur.columnIds.indexOf(responseData[i].id) != -1) {
                            responseData[i].checked = true;
                        }
                    }
                    temp.push(responseData[i]);
                }
            }
            return temp;
        };

        function onCheck(event, treeId, node) {
        }

        function onClick(event, treeId, node) {
            if(node.isParent) {
                cur.treeObj.cancelSelectedNode(node);
                cur.treeObj.expandNode(node);
                event.stopPropagation();
            }
        }

        function onExpand(event, treeId, node) {
        }

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont
            },
            check: {
                enable: true
            },
            async: {
                url: "/column/update/getPublicInfo",
                autoParam:['organId'],
                dataFilter: dataFilter
            },
            callback: {
                onClick: onClick,
                onCheck:onCheck,
                onAsyncSuccess: function () {
                    initSlimScroll();
                }
            }
        });

        function initSlimScroll() {
            //添加模拟滚动条
            var ui_tree = $('#ui_tree');
            ui_tree.attr("data-height", 260)
            App.initSlimScroll(ui_tree);
        }

        cur.treeObj = $.fn.zTree.init($("#ui_tree"), settings);
    }
    return {
        init: init
    }

}();