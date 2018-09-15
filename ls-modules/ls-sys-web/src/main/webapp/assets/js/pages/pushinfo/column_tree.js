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
            var rsp = [];
            for(var i = 0;i<data.length;i++) {
                if(data[i].isParent || data[i].type=="CMS_Site" || data[i].type=="SUB_Site") {
                    data[i].nocheck = true;
                    //temp.push(data[i]);
                    rsp.push(data[i]);
                } else if(data[i].columnTypeCode == 'articleNews' || data[i].columnTypeCode == 'pictureNews' || data[i].columnTypeCode == 'videoNews') {
                    if(cur.model.columnId == data[i].indicatorId && cur.model.cSiteId == data[i].siteId) {
                        data[i].checked = true;
                    }
                    rsp.push(data[i]);
                }
            }

            return rsp;
        };

        function onClick(event, treeId, node) {
            if(node.isParent){
                cur.treeObj.cancelSelectedNode(node);
                cur.treeObj.expandNode(node);
            } else {
                cur.treeObj.checkNode(node,!node.checked,true);
                if(node.checked) {
                    cur.vm.columnId = node.indicatorId;
                    cur.vm.cSiteId = node.siteId;
                    cur.vm.columnName = node.name;
                } else {
                    cur.vm.columnId = "";
                    cur.vm.cSiteId = "";
                    cur.vm.columnName = "";
                }
                avalon.scan();
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
                chkStyle: "radio",
                radioType:'all'
            },
            callback: {
                onClick: onClick,
                onCheck:function(event, treeId, node) {
                    if(node.checked) {
                        cur.vm.columnId = node.indicatorId;
                        cur.vm.cSiteId = node.cSiteId;
                        cur.vm.columnName = node.name;
                    } else {
                        cur.vm.columnId = "";
                        cur.vm.cSiteId = "";
                        cur.vm.columnName = "";
                    }
                    avalon.scan();
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
        ui_tree.attr("data-height", 220)
        App.initSlimScroll(ui_tree);
    }

    return {
        init: init
    }

}();