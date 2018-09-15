var column_tree = function () {

    var ztree_settings = {
        view: {
            nameIsHTML: true,
            selectedMulti: false,
            dblClickExpand: false,
            showTitle: true
        },
        check: {
            enable: true,
            chkStyle: "radio",
            radioType:'all'
        },
        async: {
            enable: true,
            type: "GET",
            dataType: "JSON",
            url: "",
            otherParam: {},
            autoParam: ""
        },
        callback: {}
    };

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    var init = function () {
        cur.map = new Map();
        function addDiyDom(treeId, node) {
        }

        function dataFilter(treeId, parentNode, responseData) {
            var data = Ls.treeDataFilter(responseData.data,Ls.treeDataType.SITE);
            for(var i = 0;i<data.length;i++) {
                if(data[i].isParent || data[i].type=="CMS_Site" || data[i].type=="SUB_Site") {
                    data[i].nocheck = true;
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
                url: "/siteMain/getColumnTreeBySite",
                otherParam: {"indicatorId": Ls.getWin.dialog.data("siteId"), "columnTypeCode": ''},
                autoParam: ["indicatorId=indicatorId"],
                dataFilter: dataFilter
            },
            callback: {
                onClick: onClick,
                beforeClick: function (treeId, treeNode, clickFlag) {
                },
                onAsyncSuccess: function () {
                    initSlimScroll();
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