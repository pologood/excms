var other_column = function () {

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
                if(data[i].isParent || data[i].type=="CMS_Site" || data[i].type=="SUB_Site") {
                    data[i].nocheck = true;
                    //temp.push(data[i]);
                } else {
                    if(cur.columnId == data[i].indicatorId && cur.cSiteId == data[i].siteId) {
                        data[i].checked = true;
                    }
                    /*if(data[i].columnTypeCode == 'articleNews') {
                     temp.push(data[i]);
                     }*/
                }
            }

            return data;
        };

        function onClick(event, treeId, node) {
            cur.type = "edit";
            cur.select_node = node;
           if(node.isParent){
                cur.treeObj.cancelSelectedNode(node);
                cur.treeObj.expandNode(node);
            } else {
               cur.treeObj.checkNode(node,!node.checked,true);
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
                url: "/msg/submit/classify/getColumnTreeBySite",
                dataFilter: dataFilter,
                otherParam: {},
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
                chkStyle: "checkbox"
            },
            callback: {
                onClick: onClick,
                beforeCheck:function(treeId, treeNode) {
                    if (treeNode.isParent) {
                        return false;
                    }
                },
                onAsyncSuccess: function () {
                    //添加模拟滚动条
                    initSlimScroll();
                }
            }
        });

        cur.treeObj = $.fn.zTree.init($("#ui_tree"), settings);

    }

    function initSlimScroll() {
        //添加模拟滚动条
        var ui_tree = $('#ui_tree');
        var ui_layout = $(".mini-layout-region-body");
        ui_tree.attr("data-height", ui_layout.height() - 40)
        App.initSlimScroll(ui_tree);
    }

    return {
        init: init
    }

}();